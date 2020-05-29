package kr.nutee.nuteebackend.Service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.nutee.nuteebackend.DTO.*;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.UpdatePostRequest;
import kr.nutee.nuteebackend.DTO.Response.*;
import kr.nutee.nuteebackend.Domain.*;
import kr.nutee.nuteebackend.Enum.ErrorCode;
import kr.nutee.nuteebackend.Exception.NotAllowedException;
import kr.nutee.nuteebackend.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class PostService {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final ImageRepository imageRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final HitRepository hitRepository;
    private final PostLikeRepository postLikeRepository;

    public List<PostShowResponse> getCategoryPosts(Long lastId, int limit, String category) {
        List<Post> posts;
        Pageable limitP = PageRequest.of(0, limit);
        if (lastId == 0) {
            posts = postRepository.findPostsByCategory(category, limitP);
        } else {
            posts = postRepository.findPostsByCategoryEqualsAndIdLessThan(category, lastId, limitP);
        }
        return transferPosts(posts);
    }

    @Transactional
    public PostResponse createPost(Long memberId, CreatePostRequest body) {
        Member member = memberRepository.findMemberById(memberId);
        Post post = fillPost(body, member);
        post = postRepository.save(post);
        saveHashTag(body, post);
        saveImage(body, post);
        return fillPostResponse(post);
    }

    @Transactional
    public PostResponse getPost(Long postId,Long memberId) {
        Post post = postRepository.findPostById(postId);
        return fillPostResponse(post);
    }

    @Transactional
    public PostResponse updatePost(Long memberId, Long postId, UpdatePostRequest body) throws NotAllowedException {
        Post post = postRepository.findPostById(postId);
        if (!post.getMember().getId().equals(memberId)) {
            throw new NotAllowedException("접근 권한이 없는 유저입니다.", ErrorCode.ACCEPT_DENIED);
        }

        post.setTitle(body.getTitle());
        post.setContent(body.getContent());

        postRepository.save(post);
        imageRepository.deleteImagesByPostId(postId);
        em.flush();
        saveImage(body, post);
        return fillPostResponse(postRepository.findPostById(postId));
    }

    @Transactional
    public PostResponse deletePost(Long postId) {
        Post post = postRepository.findPostById(postId);
        post.setDeleted(true);
        post = postRepository.save(post);
        return fillPostResponse(post);
    }

    @Transactional
    public PostResponse likePost(Long postId, Long memberId){
        Post post = postRepository.findPostById(postId);
        Member member = memberRepository.findMemberById(memberId);
        List<PostLike> likes = post.getLikes().stream()
                .filter(v->v.getMember().getId().equals(memberId))
                .collect(Collectors.toList());
        if(likes.size()==0){
            PostLike postLike = PostLike.builder()
                    .member(member)
                    .post(post)
                    .build();
            postLikeRepository.save(postLike);
        }else{
            //이미 좋아요 누름
        }
        return fillPostResponse(post);
    }

    @Transactional
    public PostResponse unlikePost(Long postId, Long memberId){
        Post post = postRepository.findPostById(postId);
        List<PostLike> like = post.getLikes().stream()
                .filter(v->v.getMember().getId().equals(memberId))
                .collect(Collectors.toList());
        if(like.size()!=0){
            System.out.println("================================================");
            post.getLikes().remove(like.get(0));
            postRepository.save(post);
            postLikeRepository.delete(like.get(0));
            em.flush();
            System.out.println("================================================");
        }else{
            //이미 좋아요 없어진 상태
        }
        return fillPostResponse(post);
    }

    @Transactional
    public PostResponse reportPost(Long postId, Long memberId, String content) {
        Member member = memberRepository.findMemberById(memberId);
        Post post = postRepository.findPostById(postId);
        List<Report> reports = reportRepository.findReportsByPostId(postId);
        if (reports.stream().anyMatch(v -> v.getMember().getId().equals(memberId))) {
            //이미 신고한 글 예외처리
        }

        //신고 등록
        Report report = Report.builder()
                .content(content)
                .member(member)
                .post(post)
                .build();
        reportRepository.save(report);

        //포스트 신고카운트
        Query query = em.createQuery("SELECT COUNT(r) FROM Report r where r.post.id = :postId", Long.class)
                .setParameter("postId", postId);
        Object count = query.getSingleResult();

        //신고횟수 넘을 시 포스트 블락
        if ((Long) count >= 5) {
            post.setBlocked(true);
            postRepository.save(post);
        }
        return fillPostResponse(post);
    }

    public List<CommentResponse> getComments(Long postId) {
        List<Comment> comments = em.createQuery(
                "SELECT c " +
                        "FROM Comment c " +
                        "WHERE c.parent IS NULL AND c.post.id = :postId AND c.isDeleted = false " +
                        "ORDER BY c.createdAt DESC", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
        return transferCommentsResponse(comments);
    }

    @Transactional
    public CommentResponse createComment(Long memberId, Long postId, String content) {
        Member member = memberRepository.findMemberById(memberId);
        Post post = postRepository.findPostById(postId);

        Comment comment = Comment.builder()
                .content(content)
                .isDeleted(false)
                .member(member)
                .post(post)
                .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .user(transferUser(comment.getMember()))
                .build();
    }

    @Transactional
    public CommentResponse updateComment(Long memberId, Long commentId, String content) {
        Comment comment = commentRepository.findCommentById(commentId);
        Member member = memberRepository.findMemberById(memberId);
        if (comment == null) {
            //예외발생(해당 댓글 없음)
        }
        if (comment.getMember() == member) {
            comment.setContent(content);
        } else {
            //예외처리(수정 권한 없음)
        }
        commentRepository.save(comment);
        return transferCommentResponse(comment);
    }

    @Transactional
    public CommentResponse createReComment(Long memberId, Long parentId, Long postId, String content) {
        Comment comment = commentRepository.findCommentById(parentId);
        Member member = memberRepository.findMemberById(memberId);
        Post post = postRepository.findPostById(postId);
        if (comment == null) {
            //예외발생(해당 댓글 없음)
        }
        Comment reComment = Comment.builder()
                .content(content)
                .isDeleted(false)
                .member(member)
                .parent(comment)
                .post(post)
                .build();
        Comment save = commentRepository.save(reComment);
        return transferCommentResponse(save);
    }

    @Transactional
    public List<CommentResponse> deleteComment(Long memberId, Long commentId, Long postId) {
        Comment comment = commentRepository.findCommentById(commentId);
        Member member = memberRepository.findMemberById(memberId);
        if (comment.getMember().getId().equals(member.getId())) {
            comment.setDeleted(true);
            commentRepository.save(comment);
        } else {
            //예외처리(수정 권한 없음)
        }
        List<Comment> comments = commentRepository.findAllCommentsByPostId(postId);
        return transferCommentsResponse(comments);
    }

    private void hitPost(Post post,Member member) {
        Hit hit = Hit.builder().member(member).post(post).build();
        System.out.println(post.getHits());
        List<Hit> memberHits = post.getHits().stream()
                .filter(v -> v.getMember().getId().equals(member.getId()))
                .collect(Collectors.toList());
        if(memberHits.size()==0){
            hitRepository.save(hit);
        }
    }

    private CommentResponse transferCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .user(transferUser(comment.getMember()))
                .reCommentResponses(transferReCommentResponses(comment))
                .updatedAt(comment.getUpdatedAt())
                .createdAt(comment.getCreatedAt())
                .content(comment.getContent())
                .build();
    }

    private List<PostShowResponse> transferPosts(List<Post> posts) {
        List<PostShowResponse> result = new ArrayList<>();
        posts.forEach(v -> result.add(PostShowResponse.builder()
                .id(v.getId())
                .category(v.getCategory())
                .images(transferImageResponses(v))
                .commentNum(transferCommentsResponse(v.getComments())==null ? 0 : transferCommentsResponse(v.getComments()).size())
                .content(v.getContent())
                .hits(transferHits(v.getHits()))
                .updatedAt(v.getUpdatedAt())
                .createdAt(v.getCreatedAt())
                .isBlocked(v.isBlocked())
                .likers(transferLikeResponses(v.getLikes()))
                .retweet(transferRetweet(v.getRetweet()))
                .title(v.getTitle())
                .user(transferUser(v.getMember()))
                .build()));
        return result;
    }

    private void saveImage(CreatePostRequest body, Post post) {
        if (body.getImages().size() != 0) {
            body.getImages().forEach(v -> imageRepository.save(Image.builder().post(post).src(v.getSrc()).build()));
        }
    }

    private void saveImage(UpdatePostRequest body, Post post) {
        if (body.getImages().size() != 0) {
            body.getImages().forEach(v -> imageRepository.save(Image.builder().post(post).src(v.getSrc()).build()));
        }
    }

    private Post fillPost(CreatePostRequest body, Member member) {
        return Post.builder()
                .content(body.getContent())
                .category(body.getCategory())
                .isBlocked(false)
                .isDeleted(false)
                .member(member)
                .title(body.getTitle())
                .build();
    }

    private Post fillPost(UpdatePostRequest body, Member member, Long postId) {
        return Post.builder()
                .id(postId)
                .title(body.getTitle())
                .content(body.getContent())
                .isBlocked(false)
                .isDeleted(false)
                .member(member)
                .build();
    }

    private void saveHashTag(CreatePostRequest body, Post post) {
        List<String> hashtags = getHashtags(body.getContent());
        if (hashtags.size() != 0) {
            hashtags.forEach(tag -> {
                if (hashtagRepository.findByName(tag) == null) {
                    Hashtag hashtag = Hashtag.builder().name(tag.substring(1).toLowerCase()).build();
                    Hashtag findHash = hashtagRepository.findByName(hashtag.getName());
                    if (findHash != null) {
                        PostHashtag postHashtag = PostHashtag.builder().hashtag(findHash).post(post).build();
                        postHashtagRepository.save(postHashtag);
                        return;
                    }
                    hashtag = hashtagRepository.save(hashtag);
                    PostHashtag postHashtag = PostHashtag.builder().hashtag(hashtag).post(post).build();
                    postHashtagRepository.save(postHashtag);
                }
            });
        }
    }

    private PostResponse fillPostResponse(Post post) {
        List<ImageResponse> imageResponses = transferImageResponses(post);
        List<PostLike> likes = postLikeRepository.findPostLikesByPostId(post.getId());
        List<User> likers = transferLikeResponses(likes);
        List<CommentResponse> comments = transferCommentsResponse(post.getComments());
        RetweetResponse retweet = transferRetweet(post.getRetweet());
        List<Hit> hitList = hitRepository.findHitsByPostId(post.getId());
        int hits = transferHits(hitList);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .hits(hits)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .isBlocked(post.isBlocked())
                .user(transferUser(post.getMember()))
                .images(imageResponses)
                .likers(likers)
                .comments(comments)
                .retweet(retweet)
                .category(post.getCategory())
                .build();
    }

    private int transferHits(List<Hit> hitList) {
        int hits;
        if(hitList==null){
            hits = 0;
        }else{
            hits = hitList.size();
        }
        return hits;
    }

    private RetweetResponse transferRetweet(Post retweet) {
        if (retweet == null) {
            return null;
        }
        return RetweetResponse.builder()
                .id(retweet.getId())
                .content(retweet.getContent())
                .hits(transferHits(retweet.getHits()))
                .commentNum(retweet.getComments().size())
                .createdAt(retweet.getCreatedAt())
                .imageResponses(transferImageResponses(retweet))
                .isBlocked(retweet.isBlocked())
                .likers(transferLikeResponses(retweet.getLikes()))
                .category(retweet.getCategory())
                .updatedAt(retweet.getUpdatedAt())
                .title(retweet.getTitle())
                .user(transferUser(retweet.getMember()))
                .build();
    }

    private List<ImageResponse> transferImageResponses(Post post) {
        if (post.getImages().size() == 0) {
            return null;
        }
        List<ImageResponse> imageResponses = new ArrayList<>();
        post.getImages().forEach(v -> imageResponses.add(transferImage(v)));
        return imageResponses;
    }

    private List<User> transferLikeResponses(List<PostLike> likes) {
        if (likes.size() == 0) {
            return null;
        }
        List<User> likers = new ArrayList<>();
        likes.forEach(v -> likers.add(
                User.builder()
                    .id(v.getMember().getId())
                .image(transferImage(v.getMember().getImage()))
                .nickname(v.getMember().getNickname())
                .build()
        ));
        return likers;
    }

    private List<CommentResponse> transferCommentsResponse(List<Comment> originComments) {
        if (originComments.size() == 0) {
            return null;
        }
        List<CommentResponse> comments = new ArrayList<>();
        originComments.stream()
                .filter(v -> !v.isDeleted())
                .filter(v->v.getParent()==null)
                .forEach(v -> comments.add(new CommentResponse(
                        v.getId(),
                        v.getContent(),
                        v.getCreatedAt(),
                        v.getUpdatedAt(),
                        transferReCommentResponses(v),
                        transferUser(v.getMember())
                )));
        return comments;
    }

    private List<ReCommentResponse> transferReCommentResponses(Comment comment) {
        List<ReCommentResponse> reComments = new ArrayList<>();
        comment.getChild().stream().filter(v -> !v.isDeleted())
                .forEach(v -> reComments.add(new ReCommentResponse(
                        v.getId(),
                        v.getContent(),
                        v.getCreatedAt(),
                        v.getUpdatedAt(),
                        transferUser(v.getMember())
                )));
        if (reComments.size() == 0) {
            return null;
        }
        return reComments;
    }

    private User transferUser(Member member) {
        return new User(
                member.getId(),
                member.getNickname(),
                transferImage(member.getImage())
        );
    }

    private ImageResponse transferImage(Image image) {
        if (image == null) {
            return null;
        }
        return new ImageResponse(
                image.getSrc()
        );
    }


    //글 내용에 해쉬태그가 있는 부분들을 리스트에 저장에 반환한다.
    public List<String> getHashtags(String content) {
        Pattern pattern = Pattern.compile("(?<!&)#(\\w+|[가-힣\\x{2712}\\x{2714}\\x{2716}\\x{271d}\\x{2721}\\x{2728}\\x{2733}\\x{2734}\\x{2744}\\x{2747}\\x{274c}\\x{274e}\\x{2753}-\\x{2755}\\x{2757}\\x{2763}\\x{2764}\\x{2795}-\\x{2797}\\x{27a1}\\x{27b0}\\x{27bf}\\x{2934}\\x{2935}\\x{2b05}-\\x{2b07}\\x{2b1b}\\x{2b1c}\\x{2b50}\\x{2b55}\\x{3030}\\x{303d}\\x{1f004}\\x{1f0cf}\\x{1f170}\\x{1f171}\\x{1f17e}\\x{1f17f}\\x{1f18e}\\x{1f191}-\\x{1f19a}\\x{1f201}\\x{1f202}\\x{1f21a}\\x{1f22f}\\x{1f232}-\\x{1f23a}\\x{1f250}\\x{1f251}\\x{1f300}-\\x{1f321}\\x{1f324}-\\x{1f393}\\x{1f396}\\x{1f397}\\x{1f399}-\\x{1f39b}\\x{1f39e}-\\x{1f3f0}\\x{1f3f3}-\\x{1f3f5}\\x{1f3f7}-\\x{1f4fd}\\x{1f4ff}-\\x{1f53d}\\x{1f549}-\\x{1f54e}\\x{1f550}-\\x{1f567}\\x{1f56f}\\x{1f570}\\x{1f573}-\\x{1f579}\\x{1f587}\\x{1f58a}-\\x{1f58d}\\x{1f590}\\x{1f595}\\x{1f596}\\x{1f5a5}\\x{1f5a8}\\x{1f5b1}\\x{1f5b2}\\x{1f5bc}\\x{1f5c2}-\\x{1f5c4}\\x{1f5d1}-\\x{1f5d3}\\x{1f5dc}-\\x{1f5de}\\x{1f5e1}\\x{1f5e3}\\x{1f5ef}\\x{1f5f3}\\x{1f5fa}-\\x{1f64f}\\x{1f680}-\\x{1f6c5}\\x{1f6cb}-\\x{1f6d0}\\x{1f6e0}-\\x{1f6e5}\\x{1f6e9}\\x{1f6eb}\\x{1f6ec}\\x{1f6f0}\\x{1f6f3}\\x{1f910}-\\x{1f918}\\x{1f980}-\\x{1f984}\\x{1f9c0}\\x{3297}\\x{3299}\\x{a9}\\x{ae}\\x{203c}\\x{2049}\\x{2122}\\x{2139}\\x{2194}-\\x{2199}\\x{21a9}\\x{21aa}\\x{231a}\\x{231b}\\x{2328}\\x{2388}\\x{23cf}\\x{23e9}-\\x{23f3}\\x{23f8}-\\x{23fa}\\x{24c2}\\x{25aa}\\x{25ab}\\x{25b6}\\x{25c0}\\x{25fb}-\\x{25fe}\\x{2600}-\\x{2604}\\x{260e}\\x{2611}\\x{2614}\\x{2615}\\x{2618}\\x{261d}\\x{2620}\\x{2622}\\x{2623}\\x{2626}\\x{262a}\\x{262e}\\x{262f}\\x{2638}-\\x{263a}\\x{2648}-\\x{2653}\\x{2660}\\x{2663}\\x{2665}\\x{2666}\\x{2668}\\x{267b}\\x{267f}\\x{2692}-\\x{2694}\\x{2696}\\x{2697}\\x{2699}\\x{269b}\\x{269c}\\x{26a0}\\x{26a1}\\x{26aa}\\x{26ab}\\x{26b0}\\x{26b1}\\x{26bd}\\x{26be}\\x{26c4}\\x{26c5}\\x{26c8}\\x{26ce}\\x{26cf}\\x{26d1}\\x{26d3}\\x{26d4}\\x{26e9}\\x{26ea}\\x{26f0}-\\x{26f5}\\x{26f7}-\\x{26fa}\\x{26fd}\\x{2702}\\x{2705}\\x{2708}-\\x{270d}\\x{270f}]|\\x{23}\\x{20e3}|\\x{2a}\\x{20e3}|\\x{30}\\x{20e3}|\\x{31}\\x{20e3}|\\x{32}\\x{20e3}|\\x{33}\\x{20e3}|\\x{34}\\x{20e3}|\\x{35}\\x{20e3}|\\x{36}\\x{20e3}|\\x{37}\\x{20e3}|\\x{38}\\x{20e3}|\\x{39}\\x{20e3}|\\x{1f1e6}[\\x{1f1e8}-\\x{1f1ec}\\x{1f1ee}\\x{1f1f1}\\x{1f1f2}\\x{1f1f4}\\x{1f1f6}-\\x{1f1fa}\\x{1f1fc}\\x{1f1fd}\\x{1f1ff}]|\\x{1f1e7}[\\x{1f1e6}\\x{1f1e7}\\x{1f1e9}-\\x{1f1ef}\\x{1f1f1}-\\x{1f1f4}\\x{1f1f6}-\\x{1f1f9}\\x{1f1fb}\\x{1f1fc}\\x{1f1fe}\\x{1f1ff}]|\\x{1f1e8}[\\x{1f1e6}\\x{1f1e8}\\x{1f1e9}\\x{1f1eb}-\\x{1f1ee}\\x{1f1f0}-\\x{1f1f5}\\x{1f1f7}\\x{1f1fa}-\\x{1f1ff}]|\\x{1f1e9}[\\x{1f1ea}\\x{1f1ec}\\x{1f1ef}\\x{1f1f0}\\x{1f1f2}\\x{1f1f4}\\x{1f1ff}]|\\x{1f1ea}[\\x{1f1e6}\\x{1f1e8}\\x{1f1ea}\\x{1f1ec}\\x{1f1ed}\\x{1f1f7}-\\x{1f1fa}]|\\x{1f1eb}[\\x{1f1ee}-\\x{1f1f0}\\x{1f1f2}\\x{1f1f4}\\x{1f1f7}]|\\x{1f1ec}[\\x{1f1e6}\\x{1f1e7}\\x{1f1e9}-\\x{1f1ee}\\x{1f1f1}-\\x{1f1f3}\\x{1f1f5}-\\x{1f1fa}\\x{1f1fc}\\x{1f1fe}]|\\x{1f1ed}[\\x{1f1f0}\\x{1f1f2}\\x{1f1f3}\\x{1f1f7}\\x{1f1f9}\\x{1f1fa}]|\\x{1f1ee}[\\x{1f1e8}-\\x{1f1ea}\\x{1f1f1}-\\x{1f1f4}\\x{1f1f6}-\\x{1f1f9}]|\\x{1f1ef}[\\x{1f1ea}\\x{1f1f2}\\x{1f1f4}\\x{1f1f5}]|\\x{1f1f0}[\\x{1f1ea}\\x{1f1ec}-\\x{1f1ee}\\x{1f1f2}\\x{1f1f3}\\x{1f1f5}\\x{1f1f7}\\x{1f1fc}\\x{1f1fe}\\x{1f1ff}]|\\x{1f1f1}[\\x{1f1e6}-\\x{1f1e8}\\x{1f1ee}\\x{1f1f0}\\x{1f1f7}-\\x{1f1fb}\\x{1f1fe}]|\\x{1f1f2}[\\x{1f1e6}\\x{1f1e8}-\\x{1f1ed}\\x{1f1f0}-\\x{1f1ff}]|\\x{1f1f3}[\\x{1f1e6}\\x{1f1e8}\\x{1f1ea}-\\x{1f1ec}\\x{1f1ee}\\x{1f1f1}\\x{1f1f4}\\x{1f1f5}\\x{1f1f7}\\x{1f1fa}\\x{1f1ff}]|\\x{1f1f4}\\x{1f1f2}|\\x{1f1f5}[\\x{1f1e6}\\x{1f1ea}-\\x{1f1ed}\\x{1f1f0}-\\x{1f1f3}\\x{1f1f7}-\\x{1f1f9}\\x{1f1fc}\\x{1f1fe}]|\\x{1f1f6}\\x{1f1e6}|\\x{1f1f7}[\\x{1f1ea}\\x{1f1f4}\\x{1f1f8}\\x{1f1fa}\\x{1f1fc}]|\\x{1f1f8}[\\x{1f1e6}-\\x{1f1ea}\\x{1f1ec}-\\x{1f1f4}\\x{1f1f7}-\\x{1f1f9}\\x{1f1fb}\\x{1f1fd}-\\x{1f1ff}]|\\x{1f1f9}[\\x{1f1e6}\\x{1f1e8}\\x{1f1e9}\\x{1f1eb}-\\x{1f1ed}\\x{1f1ef}-\\x{1f1f4}\\x{1f1f7}\\x{1f1f9}\\x{1f1fb}\\x{1f1fc}\\x{1f1ff}]|\\x{1f1fa}[\\x{1f1e6}\\x{1f1ec}\\x{1f1f2}\\x{1f1f8}\\x{1f1fe}\\x{1f1ff}]|\\x{1f1fb}[\\x{1f1e6}\\x{1f1e8}\\x{1f1ea}\\x{1f1ec}\\x{1f1ee}\\x{1f1f3}\\x{1f1fa}]|\\x{1f1fc}[\\x{1f1eb}\\x{1f1f8}]|\\x{1f1fd}\\x{1f1f0}|\\x{1f1fe}[\\x{1f1ea}\\x{1f1f9}]|\\x{1f1ff}[\\x{1f1e6}\\x{1f1f2}\\x{1f1fc}])+");
        Matcher matcher = pattern.matcher(content);

        List<String> hashtags = new ArrayList<>();
        while (matcher.find()) {
            hashtags.add(matcher.group());
        }
        return hashtags;
    }

    //해당 유저가 구독한 게시판의 글들을 가져온다.
    public List<Post> getPreferencePosts(Long id) {
        Member member = memberRepository.findMemberById(id);
        List<String> majors = member.getMajors().stream().map(Major::getMajor).collect(Collectors.toList());
        PostSearchCondition condition =
                PostSearchCondition.builder()
                        .majors(majors)
                        .build();
//        postRepository.search(condition);

        return null;
    }

}
