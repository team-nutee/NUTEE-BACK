package kr.nutee.nuteebackend.Service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.nutee.nuteebackend.DTO.*;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.RetweetRequest;
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
    private final ImageRepository imageRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final Transfer transfer;

    public List<PostShowResponse> getCategoryPosts(Long lastId, int limit, String category) {
        List<Post> posts;
        Pageable limitP = PageRequest.of(0, limit);
        if (lastId == 0) {
            posts = postRepository.findPostsByCategory(category, limitP);
        } else {
            posts = postRepository.findPostsByCategoryEqualsAndIdLessThan(category, lastId, limitP);
        }
        return transfer.transferPosts(posts);
    }

    @Transactional
    public PostResponse createPost(Long memberId, CreatePostRequest body) {
        Member member = memberRepository.findMemberById(memberId);
        Post post = transfer.fillPost(body, member);
        post = postRepository.save(post);
        transfer.saveHashTag(body.getContent(), post);
        transfer.saveImage(body, post);
        return transfer.transferPost(post);
    }

    @Transactional
    public PostResponse getPost(Long postId,Long memberId) {
        Member member = memberRepository.findMemberById(memberId);
        Post post = postRepository.findPostById(postId);
        transfer.hitPost(post,member);
        return transfer.transferPost(post);
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
        transfer.saveImage(body, post);
        return transfer.transferPost(postRepository.findPostById(postId));
    }

    @Transactional
    public PostResponse deletePost(Long postId) {
        Post post = postRepository.findPostById(postId);
        post.setDeleted(true);
        post = postRepository.save(post);
        return transfer.transferPost(post);
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
        return transfer.transferPost(post);
    }

    @Transactional
    public PostResponse unlikePost(Long postId, Long memberId){
        Post post = postRepository.findPostById(postId);
        List<PostLike> like = post.getLikes().stream()
                .filter(v->v.getMember().getId().equals(memberId))
                .collect(Collectors.toList());
        if(like.size()!=0){
            post.getLikes().remove(like.get(0));
            postRepository.save(post);
            postLikeRepository.delete(like.get(0));
            em.flush();
        }else{
            //이미 좋아요 없어진 상태
        }
        return transfer.transferPost(post);
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
        return transfer.transferPost(post);
    }

    public List<PostShowResponse> searchPost(Long lastId, int limit, String text){
        List<Post> posts;
        Pageable limitP = PageRequest.of(0, limit);
        if (lastId == 0) {
            posts = postRepository.findPostsByText(text, limitP);
        } else {
            posts = postRepository.findPostsByTextAndIdLessThan(text, lastId, limitP);
        }
        return transfer.transferPosts(posts);
    }

    /*
     글 생성 및 수정 로직에 따라서
     */
    @Transactional
    public PostResponse createRetweet(Long postId, Long memberId, RetweetRequest body){
        Post retweet = postRepository.findPostById(postId);
        Member member = memberRepository.findMemberById(memberId);
        Post post = Post.builder()
                .title(body.getTitle())
                .content(body.getContent())
                .images(new ArrayList<>())
                .member(member)
                .comments(new ArrayList<>())
                .isDeleted(false)
                .isBlocked(false)
                .category(body.getCategory())
                .retweet(retweet)
                .build();

        post = postRepository.save(post);
        transfer.saveHashTag(body.getContent(), post);
        return transfer.transferPost(post);
    }

    public List<CommentResponse> getComments(Long postId) {
        List<Comment> comments = em.createQuery(
                "SELECT c " +
                        "FROM Comment c " +
                        "WHERE c.parent IS NULL AND c.post.id = :postId AND c.isDeleted = false " +
                        "ORDER BY c.createdAt DESC", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
        return transfer.transferCommentsResponse(comments);
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
                .user(transfer.transferUser(comment.getMember()))
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
        return transfer.transferCommentResponse(comment);
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
        return transfer.transferCommentResponse(save);
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
        return transfer.transferCommentsResponse(comments);
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
