package kr.nutee.nuteebackend.Service;


import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.RetweetRequest;
import kr.nutee.nuteebackend.DTO.Request.UpdatePostRequest;
import kr.nutee.nuteebackend.DTO.Response.*;
import kr.nutee.nuteebackend.Domain.*;
import kr.nutee.nuteebackend.Enum.ErrorCode;
import kr.nutee.nuteebackend.Enum.InterestCategory;
import kr.nutee.nuteebackend.Enum.MajorCategory;
import kr.nutee.nuteebackend.Exception.BusinessException;
import kr.nutee.nuteebackend.Exception.NotAllowedException;
import kr.nutee.nuteebackend.Exception.NotExistException;
import kr.nutee.nuteebackend.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class PostService {

    @PersistenceContext
    EntityManager em;

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final PostReportRepository postReportRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final Util util;

    private static final int REPORT_COUNT = 5;

    public List<PostShowResponse> getCategoryPosts(Long lastId, int limit, String category) {
        List<Post> posts;
        Pageable limitP = PageRequest.of(0, limit);
        if (lastId == 0) {
            posts = postRepository.findPostsByCategory(category, limitP);
        } else {
            posts = postRepository.findPostsByCategoryEqualsAndIdLessThan(category, lastId, limitP);
        }
        return util.transformPosts(posts);
    }

    public List<PostShowResponse> getAllPosts(Long lastId, int limit) {
        List<Post> posts;
        Pageable limitP = PageRequest.of(0, limit);
        if (lastId == 0) {
            posts = postRepository.findAllPosts(limitP);
        } else {
            posts = postRepository.findAllPostsAndIdLessThan(lastId, limitP);
        }
        return util.transformPosts(posts);
    }

    @Transactional
    public PostResponse createPost(Long memberId, CreatePostRequest body) {
        if (!isCorrectCategory(body.getCategory())) {
            throw new IllegalArgumentException("서버에 해당하는 카테고리가 존재하지 않습니다.");
        }
        Member member = memberRepository.findMemberById(memberId);
        Post post = util.fillPost(body, member);
        post = postRepository.save(post);
        util.saveHashTag(body.getContent(), post);
        post.setImages(util.saveImage(body, post));
        return util.transformPost(post);
    }

    @Transactional
    public PostResponse getPost(Long postId,Long memberId) throws BusinessException {
        Member member = memberRepository.findMemberById(memberId);
        if (member==null) {
            throw new NotExistException("존재하지 않는 사용자 입니다.", ErrorCode.NOT_EXIST, HttpStatus.NOT_FOUND,memberId);
        }
        Post post = postRepository.findPostById(postId);
        if(post.isDeleted()){
            throw new NotExistException("존재 하지 않는 글 입니다.", ErrorCode.NOT_EXIST, HttpStatus.NOT_FOUND,postId);
        }
        if(post.isBlocked()){
            throw new NotAllowedException("현재 신고로 인하여 볼 수 없는 글 입니다.",ErrorCode.ACCEPT_DENIED, HttpStatus.FORBIDDEN,postId);
        }
        util.hitPost(post,member);
        return util.transformPost(post);
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long memberId, UpdatePostRequest body) throws NotAllowedException {
        Post post = postRepository.findPostById(postId);
        if (!post.getMember().getId().equals(memberId)) {
            throw new NotAllowedException("접근 권한이 없는 유저 입니다.", ErrorCode.ACCEPT_DENIED, HttpStatus.FORBIDDEN, postId);
        }

        post.setTitle(body.getTitle());
        post.setContent(body.getContent());

        post = postRepository.save(post);
        imageRepository.deleteImagesByPostId(postId);
        em.flush();
        post.setImages(util.saveImage(body, post));
        return util.transformPost(post);
    }

    @Transactional
    public Map<String,Long> deletePost(Long postId, Long memberId) {
        Post post = postRepository.findPostById(postId);
        if (!post.getMember().getId().equals(memberId)) {
            throw new NotAllowedException("접근 권한이 없는 유저 입니다.", ErrorCode.ACCEPT_DENIED, HttpStatus.FORBIDDEN);
        }
        post.setDeleted(true);
        post = postRepository.save(post);
        Map<String,Long> map = new HashMap<>();
        map.put("id",post.getId());
        return map;
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
        return util.transformPost(post);
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
        return util.transformPost(post);
    }

    @Transactional
    public PostResponse reportPost(Long postId, Long memberId, String content) {
        Member member = memberRepository.findMemberById(memberId);
        Post post = postRepository.findPostById(postId);
        List<PostReport> postReports = postReportRepository.findPostReportsByPostId(postId);
        if (postReports.stream().anyMatch(v -> v.getMember().getId().equals(memberId))) {
            //이미 신고한 글 예외처리
        }

        //신고 등록
        PostReport postReport = PostReport.builder()
                .content(content)
                .member(member)
                .post(post)
                .build();
        postReportRepository.save(postReport);

        //포스트 신고카운트
        Query query = em.createQuery("SELECT COUNT(r) FROM PostReport r where r.post.id = :postId", Long.class)
                .setParameter("postId", postId);
        Object count = query.getSingleResult();

        //신고횟수 넘을 시 포스트 블락
        if ((Long) count >= 5) {
            post.setBlocked(true);
            postRepository.save(post);
        }
        return util.transformPost(post);
    }

    public List<PostShowResponse> searchPost(Long lastId, int limit, String text){
        List<Post> posts;
        Pageable limitP = PageRequest.of(0, limit);
        if (lastId == 0) {
            posts = postRepository.findPostsByText(text, limitP);
        } else {
            posts = postRepository.findPostsByTextAndIdLessThan(text, lastId, limitP);
        }
        return util.transformPosts(posts);
    }

    public List<PostShowResponse> getHashtagPosts(Long lastId, int limit, String tag){
        List<Post> posts;
        Pageable limitP = PageRequest.of(0, limit);
        Hashtag hashtag = hashtagRepository.findByName(tag);
        if(hashtag == null){
            //결과값 없음
            return null;
        }else{
            Long hashtagId = hashtagRepository.findByName(tag).getId();
            if (lastId == 0) {
                posts = postRepository.findPostsByHashtagId(hashtagId, limitP);
            } else {
                posts = postRepository.findPostsByHashtagIdAndIdLessThan(hashtagId, lastId, limitP);
            }
        }
        return util.transformPosts(posts);
    }

    //해당 유저가 구독한 게시판의 글들을 가져온다.
    public List<PostShowResponse> getFavoritePosts(Long lastId, int limit, Long memberId) {
        List<String> favorites = new ArrayList<>();
        List<Post> favoritePosts = new ArrayList<>();
        Pageable limitP = PageRequest.of(0, limit);
        Member member = memberRepository.findMemberById(memberId);

        member.getInterests().forEach(v->favorites.add(v.getInterest()));
        List<Post> finalPreferPosts = favoritePosts;
        favorites.forEach(v-> {
            if (lastId == 0) {
                finalPreferPosts.addAll(postRepository.findPostsByCategory(v, limitP));
            } else {
                finalPreferPosts.addAll(postRepository.findPostsByCategoryEqualsAndIdLessThan(v,lastId,limitP));
            }
        });
        favoritePosts = favoritePosts.stream()
                .sorted()
                .collect(Collectors.toList());

        return util.transformPosts(favoritePosts).stream()
                .limit(limit).collect(Collectors.toList());
    }

    //해당 유저가 구독한 전공 게시판의 글들을 가져온다.
    public List<PostShowResponse> getMajorPosts(Long lastId, int limit, Long memberId) {
        List<String> favorites = new ArrayList<>();
        List<Post> favoritePosts = new ArrayList<>();
        Pageable limitP = PageRequest.of(0, limit);
        Member member = memberRepository.findMemberById(memberId);

        member.getMajors().forEach(v->favorites.add(v.getMajor()));
        List<Post> finalPreferPosts = favoritePosts;
        favorites.forEach(v-> {
            if (lastId == 0) {
                finalPreferPosts.addAll(postRepository.findPostsByCategory(v, limitP));
            } else {
                finalPreferPosts.addAll(postRepository.findPostsByCategoryEqualsAndIdLessThan(v,lastId,limitP));
            }
        });
        favoritePosts = favoritePosts.stream()
            .sorted()
            .collect(Collectors.toList());

        return util.transformPosts(favoritePosts).stream()
            .limit(limit).collect(Collectors.toList());
    }

    public List<PostShowResponse> getUserPosts(Long memberId, int limit, Long lastId) {
        Pageable limitP = PageRequest.of(0, limit);
        List<Post> posts;

        if (lastId == 0) {
            posts = postRepository.findPostsByMemberId(memberId, limitP);
        } else {
            posts = postRepository.findPostsByMemberIdAndIdLessThan(memberId, lastId, limitP);
        }
        return util.transformPosts(posts);
    }

    public List<PostShowResponse> getUserCommentPosts(Long memberId, int limit, Long lastId) {
        Pageable limitP = PageRequest.of(0, limit);
        List<Post> posts;

        if (lastId == 0) {
            posts = postRepository.findUserCommentPosts(memberId, limitP);
        } else {
            posts = postRepository.findUserCommentPostsAndIdLessThan(memberId, lastId, limitP);
        }
        return util.transformPosts(posts);
    }

    public List<PostShowResponse> getUserLikePosts(Long memberId, int limit, Long lastId) {
        Pageable limitP = PageRequest.of(0, limit);
        List<Post> posts;

        if (lastId == 0) {
            posts = postRepository.findUserLikePosts(memberId, limitP);
        } else {
            posts = postRepository.findUserLikePostsAndIdLessThan(memberId, lastId, limitP);
        }
        return util.transformPosts(posts);
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
        util.saveHashTag(body.getContent(), post);
        return util.transformPost(post);
    }

    public List<CommentResponse> getComments(Long postId) {
        List<Comment> comments = em.createQuery(
                "SELECT c " +
                        "FROM Comment c " +
                        "WHERE c.parent IS NULL AND c.post.id = :postId AND c.isDeleted = false " +
                        "ORDER BY c.createdAt DESC", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
        return util.transformCommentsResponse(comments);
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
                .user(util.transformUser(comment.getMember()))
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
        return util.transformCommentResponse(comment);
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
        return util.transformCommentResponse(save);
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
        return util.transformCommentsResponse(comments);
    }

    @Transactional
    public CommentResponse reportComment(Long commentId, Long memberId, String content) {
        Member member = memberRepository.findMemberById(memberId);
        Comment comment = commentRepository.findCommentById(commentId);
        List<CommentReport> commentReports = commentReportRepository.findCommentReportsByCommentId(commentId);
        if (commentReports.stream().anyMatch(v -> v.getMember().getId().equals(memberId))) {
            //이미 신고한 댓글 예외처리
        }

        //신고 등록
        CommentReport commentReport = CommentReport.builder()
            .content(content)
            .member(member)
            .comment(comment)
            .build();
        commentReportRepository.save(commentReport);

        //댓글 신고카운트
        Query query = em.createQuery("SELECT COUNT(r) FROM CommentReport r where r.comment.id = :commentId", Long.class)
            .setParameter("commentId", commentId);
        Object count = query.getSingleResult();

        //신고횟수 넘을 시 코멘트 블락
        if ((Long) count >= REPORT_COUNT) {
            comment.setBlocked(true);
            commentRepository.save(comment);
        }
        return util.transformCommentResponse(comment);
    }

    @Transactional
    public CommentResponse likeComment(Long commentId, Long memberId){
        Comment comment = commentRepository.findCommentById(commentId);
        Member member = memberRepository.findMemberById(memberId);
        List<CommentLike> likes = comment.getLikes().stream()
            .filter(v->v.getMember().getId().equals(memberId))
            .collect(Collectors.toList());
        if(likes.size()==0){
            CommentLike commentLike = CommentLike.builder()
                .member(member)
                .comment(comment)
                .build();
            comment.getLikes().add(commentLikeRepository.save(commentLike));
            commentRepository.save(comment);
        }else{
            //이미 좋아요 누름
        }
        Comment changedComment = commentRepository.findCommentById(commentId);
        return util.transformCommentResponse(changedComment);
    }

    @Transactional
    public CommentResponse unlikeComment(Long commentId, Long memberId){
        Comment comment = commentRepository.findCommentById(commentId);
        List<CommentLike> likes = comment.getLikes().stream()
            .filter(v->v.getMember().getId().equals(memberId))
            .collect(Collectors.toList());
        if(likes.size()!=0){
            comment.getLikes().remove(likes.get(0));
            commentRepository.save(comment);
            commentLikeRepository.delete(likes.get(0));
            em.flush();
        }else{
            //이미 좋아요 없어진 상태
        }
        Comment changedComment = commentRepository.findCommentById(commentId);
        return util.transformCommentResponse(changedComment);
    }

    private boolean isCorrectCategory(String category) {
        for (InterestCategory value : InterestCategory.values()) {
            if (value.getInterest().equals(category)) {
                return true;
            }
        }
        for (MajorCategory value : MajorCategory.values()) {
            if (value.getMajor().equals(category)) {
                return true;
            }
        }
        return false;
    }

}
