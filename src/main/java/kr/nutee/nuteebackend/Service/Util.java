package kr.nutee.nuteebackend.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.UpdatePostRequest;
import kr.nutee.nuteebackend.DTO.Response.*;
import kr.nutee.nuteebackend.Domain.*;
import kr.nutee.nuteebackend.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class Util {

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final HitRepository hitRepository;
    private final ImageRepository imageRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    public void hitPost(Post post, Member member) {
        Hit hit = Hit.builder().member(member).post(post).build();
        List<Hit> memberHits = post.getHits().stream()
                .filter(v -> v.getMember().getId().equals(member.getId()))
                .collect(Collectors.toList());
        if(memberHits.size()==0){
            hitRepository.save(hit);
        }
    }

    public CommentResponse transformCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .user(transformUser(comment.getMember()))
                .likers(transformCommentLikeResponses(comment.getLikes()))
                .reComment(transformReCommentResponses(comment))
                .updatedAt(comment.getUpdatedAt())
                .createdAt(comment.getCreatedAt())
                .content(comment.getContent())
                .build();
    }

    public List<PostShowResponse> transformPosts(List<Post> posts) {
        List<PostShowResponse> result = new ArrayList<>();

        posts.forEach(v -> result.add(PostShowResponse.builder()
                .id(v.getId())
                .category(v.getCategory())
                .images(transformImageResponses(v))
                .commentNum(getRealCommentNum(v))
                .content(v.getContent())
                .hits(transformHits(v.getHits()))
                .updatedAt(v.getUpdatedAt())
                .createdAt(v.getCreatedAt())
                .isBlocked(v.isBlocked())
                .likers(transformPostLikeResponses(v.getLikes()))
                .retweet(transformRetweet(v.getRetweet()))
                .title(v.getTitle())
                .user(transformUser(v.getMember()))
                .build()));
        return result;
    }

    private int getRealCommentNum(Post post) {
        List<Comment> comments = commentRepository.findAllCommentsByPostId(post.getId());

        int commentSize = comments.size();

        for (Comment comment : comments) {
            commentSize += commentRepository.countAllReCommentsByCommentId(comment.getId());
        }
        return commentSize;
    }

    public List<Image> saveImage(CreatePostRequest body, Post post) {
        List<Image> images  = new ArrayList<>();
        if(body.getImages()==null){
            return null;
        }
        if (body.getImages().size() != 0) {
            body.getImages().forEach(v -> images.add(imageRepository.save(Image.builder().post(post).src(v.getSrc()).build())));
        }
        return images;
    }

    public List<Image> saveImage(UpdatePostRequest body, Post post) {
        List<Image> images  = new ArrayList<>();
        if(body.getImages()==null){
            return null;
        }
        if (body.getImages().size() != 0) {
            body.getImages().forEach(v -> images.add(imageRepository.save(Image.builder().post(post).src(v.getSrc()).build())));
        }
        return images;
    }

    public Post fillPost(CreatePostRequest body, Member member) {
        return Post.builder()
                .content(body.getContent())
                .category(body.getCategory())
                .isBlocked(false)
                .isDeleted(false)
                .member(member)
                .title(body.getTitle())
                .build();
    }

    public void saveHashTag(String content, Post post) {
        List<String> hashtags = getHashtags(content);
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

    public PostResponse transformPost(Post post) {
        List<ImageResponse> imageResponses = transformImageResponses(post);
        List<PostLike> likes = postLikeRepository.findPostLikesByPostId(post.getId());
        List<User> likers = transformPostLikeResponses(likes);
        List<CommentResponse> comments = transformCommentsResponse(commentRepository.findAllCommentsByPostId(post.getId()));
        RetweetResponse retweet = transformRetweet(post.getRetweet());
        List<Hit> hitList = hitRepository.findHitsByPostId(post.getId());
        int hits = transformHits(hitList);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .hits(hits)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .isBlocked(post.isBlocked())
                .user(transformUser(post.getMember()))
                .images(imageResponses)
                .likers(likers)
                .comments(comments)
                .retweet(retweet)
                .category(post.getCategory())
                .build();
    }

    public int transformHits(List<Hit> hitList) {
        int hits;
        if(hitList==null){
            hits = 0;
        }else{
            hits = hitList.size();
        }
        return hits;
    }

    public RetweetResponse transformRetweet(Post retweet) {
        if (retweet == null) {
            return null;
        }
        return RetweetResponse.builder()
                .id(retweet.getId())
                .content(retweet.getContent())
                .hits(transformHits(retweet.getHits()))
                .commentNum(retweet.getComments().size())
                .createdAt(retweet.getCreatedAt())
                .images(transformImageResponses(retweet))
                .isDeleted(retweet.isDeleted())
                .isBlocked(retweet.isBlocked())
                .likers(transformPostLikeResponses(retweet.getLikes()))
                .category(retweet.getCategory())
                .updatedAt(retweet.getUpdatedAt())
                .title(retweet.getTitle())
                .user(transformUser(retweet.getMember()))
                .build();
    }

    public List<ImageResponse> transformImageResponses(Post post) {
        if(post.getImages()==null){
            return null;
        }
        if (post.getImages().size() == 0) {
            return null;
        }
        List<ImageResponse> imageResponses = new ArrayList<>();
        post.getImages().forEach(v -> imageResponses.add(transformImage(v)));
        return imageResponses;
    }

    public List<User> transformPostLikeResponses(List<PostLike> likes) {
        if (likes.size() == 0) {
            return null;
        }
        List<User> likers = new ArrayList<>();
        likes.forEach(v -> likers.add(
                User.builder()
                        .id(v.getMember().getId())
                        .image(transformImage(v.getMember().getImage()))
                        .nickname(v.getMember().getNickname())
                        .build()
        ));
        return likers;
    }

    public List<User> transformCommentLikeResponses(List<CommentLike> likes) {
        if (likes.size() == 0) {
            return null;
        }
        List<User> likers = new ArrayList<>();
        likes.forEach(v -> likers.add(
            User.builder()
                .id(v.getMember().getId())
                .image(transformImage(v.getMember().getImage()))
                .nickname(v.getMember().getNickname())
                .build()
        ));
        return likers;
    }

    public List<CommentResponse> transformCommentsResponse(List<Comment> originComments) {
        if (originComments==null){
            return null;
        }
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
                        v.getLikes().stream().map(CommentLike::getMember).map(this::transformUser).collect(Collectors.toList()),
                        v.getCreatedAt(),
                        v.getUpdatedAt(),
                        transformReCommentResponses(v),
                        transformUser(v.getMember())
                )));
        return comments;
    }

    public List<ReCommentResponse> transformReCommentResponses(Comment comment) {
        List<ReCommentResponse> reComments = new ArrayList<>();
        comment.getChild().stream().filter(v -> !v.isDeleted())
                .forEach(v -> reComments.add(new ReCommentResponse(
                        v.getId(),
                        v.getContent(),
                        v.getLikes().stream().map(CommentLike::getMember).map(this::transformUser).collect(Collectors.toList()),
                        v.getCreatedAt(),
                        v.getUpdatedAt(),
                        transformUser(v.getMember())
                )));
        if (reComments.size() == 0) {
            return null;
        }
        return reComments;
    }

    public User transformUser(Member member) {
        return new User(
                member.getId(),
                member.getNickname(),
                transformImage(member.getImage())
        );
    }

    public ImageResponse transformImage(Image image) {
        if (image == null) {
            return null;
        }
        return new ImageResponse(
                image.getSrc()
        );
    }


    //글 내용에 해쉬태그가 있는 부분들을 리스트에 저장에 반환한다.
    public List<String> getHashtags(String content) {
        Pattern pattern = Pattern.compile("(?<!&)#(\\w+|[ㄱ-ㅎㅏ-ㅣ가-힣\\x{2712}\\x{2714}\\x{2716}\\x{271d}\\x{2721}\\x{2728}\\x{2733}\\x{2734}\\x{2744}\\x{2747}\\x{274c}\\x{274e}\\x{2753}-\\x{2755}\\x{2757}\\x{2763}\\x{2764}\\x{2795}-\\x{2797}\\x{27a1}\\x{27b0}\\x{27bf}\\x{2934}\\x{2935}\\x{2b05}-\\x{2b07}\\x{2b1b}\\x{2b1c}\\x{2b50}\\x{2b55}\\x{3030}\\x{303d}\\x{1f004}\\x{1f0cf}\\x{1f170}\\x{1f171}\\x{1f17e}\\x{1f17f}\\x{1f18e}\\x{1f191}-\\x{1f19a}\\x{1f201}\\x{1f202}\\x{1f21a}\\x{1f22f}\\x{1f232}-\\x{1f23a}\\x{1f250}\\x{1f251}\\x{1f300}-\\x{1f321}\\x{1f324}-\\x{1f393}\\x{1f396}\\x{1f397}\\x{1f399}-\\x{1f39b}\\x{1f39e}-\\x{1f3f0}\\x{1f3f3}-\\x{1f3f5}\\x{1f3f7}-\\x{1f4fd}\\x{1f4ff}-\\x{1f53d}\\x{1f549}-\\x{1f54e}\\x{1f550}-\\x{1f567}\\x{1f56f}\\x{1f570}\\x{1f573}-\\x{1f579}\\x{1f587}\\x{1f58a}-\\x{1f58d}\\x{1f590}\\x{1f595}\\x{1f596}\\x{1f5a5}\\x{1f5a8}\\x{1f5b1}\\x{1f5b2}\\x{1f5bc}\\x{1f5c2}-\\x{1f5c4}\\x{1f5d1}-\\x{1f5d3}\\x{1f5dc}-\\x{1f5de}\\x{1f5e1}\\x{1f5e3}\\x{1f5ef}\\x{1f5f3}\\x{1f5fa}-\\x{1f64f}\\x{1f680}-\\x{1f6c5}\\x{1f6cb}-\\x{1f6d0}\\x{1f6e0}-\\x{1f6e5}\\x{1f6e9}\\x{1f6eb}\\x{1f6ec}\\x{1f6f0}\\x{1f6f3}\\x{1f910}-\\x{1f918}\\x{1f980}-\\x{1f984}\\x{1f9c0}\\x{3297}\\x{3299}\\x{a9}\\x{ae}\\x{203c}\\x{2049}\\x{2122}\\x{2139}\\x{2194}-\\x{2199}\\x{21a9}\\x{21aa}\\x{231a}\\x{231b}\\x{2328}\\x{2388}\\x{23cf}\\x{23e9}-\\x{23f3}\\x{23f8}-\\x{23fa}\\x{24c2}\\x{25aa}\\x{25ab}\\x{25b6}\\x{25c0}\\x{25fb}-\\x{25fe}\\x{2600}-\\x{2604}\\x{260e}\\x{2611}\\x{2614}\\x{2615}\\x{2618}\\x{261d}\\x{2620}\\x{2622}\\x{2623}\\x{2626}\\x{262a}\\x{262e}\\x{262f}\\x{2638}-\\x{263a}\\x{2648}-\\x{2653}\\x{2660}\\x{2663}\\x{2665}\\x{2666}\\x{2668}\\x{267b}\\x{267f}\\x{2692}-\\x{2694}\\x{2696}\\x{2697}\\x{2699}\\x{269b}\\x{269c}\\x{26a0}\\x{26a1}\\x{26aa}\\x{26ab}\\x{26b0}\\x{26b1}\\x{26bd}\\x{26be}\\x{26c4}\\x{26c5}\\x{26c8}\\x{26ce}\\x{26cf}\\x{26d1}\\x{26d3}\\x{26d4}\\x{26e9}\\x{26ea}\\x{26f0}-\\x{26f5}\\x{26f7}-\\x{26fa}\\x{26fd}\\x{2702}\\x{2705}\\x{2708}-\\x{270d}\\x{270f}]|\\x{23}\\x{20e3}|\\x{2a}\\x{20e3}|\\x{30}\\x{20e3}|\\x{31}\\x{20e3}|\\x{32}\\x{20e3}|\\x{33}\\x{20e3}|\\x{34}\\x{20e3}|\\x{35}\\x{20e3}|\\x{36}\\x{20e3}|\\x{37}\\x{20e3}|\\x{38}\\x{20e3}|\\x{39}\\x{20e3}|\\x{1f1e6}[\\x{1f1e8}-\\x{1f1ec}\\x{1f1ee}\\x{1f1f1}\\x{1f1f2}\\x{1f1f4}\\x{1f1f6}-\\x{1f1fa}\\x{1f1fc}\\x{1f1fd}\\x{1f1ff}]|\\x{1f1e7}[\\x{1f1e6}\\x{1f1e7}\\x{1f1e9}-\\x{1f1ef}\\x{1f1f1}-\\x{1f1f4}\\x{1f1f6}-\\x{1f1f9}\\x{1f1fb}\\x{1f1fc}\\x{1f1fe}\\x{1f1ff}]|\\x{1f1e8}[\\x{1f1e6}\\x{1f1e8}\\x{1f1e9}\\x{1f1eb}-\\x{1f1ee}\\x{1f1f0}-\\x{1f1f5}\\x{1f1f7}\\x{1f1fa}-\\x{1f1ff}]|\\x{1f1e9}[\\x{1f1ea}\\x{1f1ec}\\x{1f1ef}\\x{1f1f0}\\x{1f1f2}\\x{1f1f4}\\x{1f1ff}]|\\x{1f1ea}[\\x{1f1e6}\\x{1f1e8}\\x{1f1ea}\\x{1f1ec}\\x{1f1ed}\\x{1f1f7}-\\x{1f1fa}]|\\x{1f1eb}[\\x{1f1ee}-\\x{1f1f0}\\x{1f1f2}\\x{1f1f4}\\x{1f1f7}]|\\x{1f1ec}[\\x{1f1e6}\\x{1f1e7}\\x{1f1e9}-\\x{1f1ee}\\x{1f1f1}-\\x{1f1f3}\\x{1f1f5}-\\x{1f1fa}\\x{1f1fc}\\x{1f1fe}]|\\x{1f1ed}[\\x{1f1f0}\\x{1f1f2}\\x{1f1f3}\\x{1f1f7}\\x{1f1f9}\\x{1f1fa}]|\\x{1f1ee}[\\x{1f1e8}-\\x{1f1ea}\\x{1f1f1}-\\x{1f1f4}\\x{1f1f6}-\\x{1f1f9}]|\\x{1f1ef}[\\x{1f1ea}\\x{1f1f2}\\x{1f1f4}\\x{1f1f5}]|\\x{1f1f0}[\\x{1f1ea}\\x{1f1ec}-\\x{1f1ee}\\x{1f1f2}\\x{1f1f3}\\x{1f1f5}\\x{1f1f7}\\x{1f1fc}\\x{1f1fe}\\x{1f1ff}]|\\x{1f1f1}[\\x{1f1e6}-\\x{1f1e8}\\x{1f1ee}\\x{1f1f0}\\x{1f1f7}-\\x{1f1fb}\\x{1f1fe}]|\\x{1f1f2}[\\x{1f1e6}\\x{1f1e8}-\\x{1f1ed}\\x{1f1f0}-\\x{1f1ff}]|\\x{1f1f3}[\\x{1f1e6}\\x{1f1e8}\\x{1f1ea}-\\x{1f1ec}\\x{1f1ee}\\x{1f1f1}\\x{1f1f4}\\x{1f1f5}\\x{1f1f7}\\x{1f1fa}\\x{1f1ff}]|\\x{1f1f4}\\x{1f1f2}|\\x{1f1f5}[\\x{1f1e6}\\x{1f1ea}-\\x{1f1ed}\\x{1f1f0}-\\x{1f1f3}\\x{1f1f7}-\\x{1f1f9}\\x{1f1fc}\\x{1f1fe}]|\\x{1f1f6}\\x{1f1e6}|\\x{1f1f7}[\\x{1f1ea}\\x{1f1f4}\\x{1f1f8}\\x{1f1fa}\\x{1f1fc}]|\\x{1f1f8}[\\x{1f1e6}-\\x{1f1ea}\\x{1f1ec}-\\x{1f1f4}\\x{1f1f7}-\\x{1f1f9}\\x{1f1fb}\\x{1f1fd}-\\x{1f1ff}]|\\x{1f1f9}[\\x{1f1e6}\\x{1f1e8}\\x{1f1e9}\\x{1f1eb}-\\x{1f1ed}\\x{1f1ef}-\\x{1f1f4}\\x{1f1f7}\\x{1f1f9}\\x{1f1fb}\\x{1f1fc}\\x{1f1ff}]|\\x{1f1fa}[\\x{1f1e6}\\x{1f1ec}\\x{1f1f2}\\x{1f1f8}\\x{1f1fe}\\x{1f1ff}]|\\x{1f1fb}[\\x{1f1e6}\\x{1f1e8}\\x{1f1ea}\\x{1f1ec}\\x{1f1ee}\\x{1f1f3}\\x{1f1fa}]|\\x{1f1fc}[\\x{1f1eb}\\x{1f1f8}]|\\x{1f1fd}\\x{1f1f0}|\\x{1f1fe}[\\x{1f1ea}\\x{1f1f9}]|\\x{1f1ff}[\\x{1f1e6}\\x{1f1f2}\\x{1f1fc}])+");
        Matcher matcher = pattern.matcher(content);

        List<String> hashtags = new ArrayList<>();
        while (matcher.find()) {
            hashtags.add(matcher.group());
        }
        return hashtags;
    }

    public Long getTokenMemberId(HttpServletRequest request){
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.convertValue(request.getAttribute("user"),Map.class);
        return Long.parseLong(map.get("id").toString());
    }
}
