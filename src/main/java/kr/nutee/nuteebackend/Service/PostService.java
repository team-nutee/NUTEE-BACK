package kr.nutee.nuteebackend.Service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.nutee.nuteebackend.DTO.*;
import kr.nutee.nuteebackend.DTO.Request.PostRequest;
import kr.nutee.nuteebackend.DTO.Response.*;
import kr.nutee.nuteebackend.Domain.*;
import kr.nutee.nuteebackend.Repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    @Autowired
    PostRepository postRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    PostHashtagRepository postHashtagRepository;

    @Autowired
    ImageRepository imageRepository;

    @Transactional
    public PostResponse createPost(Long memberId, PostRequest body){
        Member member = memberRepository.findMemberById(memberId);
        Post post = fillPost(body, member);
        post = postRepository.save(post);
        saveHashTag(body, post);
        saveImage(body, post);
        return fillPostResponse(post);
    }

    private void saveImage(PostRequest body, Post post) {
        if(body.getImages().size()!=0){
            body.getImages().forEach(v->imageRepository.save(Image.builder().post(post).src(v.getSrc()).build()));
        }
    }

    private Post fillPost(PostRequest body, Member member) {
        return Post.builder()
                    .content(body.getContent())
                    .interest(body.getInterest())
                    .major(body.getMajor())
                    .isBlocked(false)
                    .isDeleted(false)
                    .member(member)
                    .title(body.getTitle())
                    .build();
    }

    private void saveHashTag(PostRequest body, Post post) {
        List<String> hashtags = getHashtags(body.getContent());
        if(hashtags.size()!=0){
            hashtags.forEach(tag->{
                if(hashtagRepository.findByName(tag)==null){
                    Hashtag hashtag = Hashtag.builder().name(tag.substring(1).toLowerCase()).build();
                    Hashtag findHash = hashtagRepository.findByName(hashtag.getName());
                    if(findHash != null){
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
        List<LikeResponse> likers = transferLikeResponses(post);
        List<ReCommentResponse> reComments = transferReCommentResponses(post);
        List<CommentResponse> comments = transferCommentResponses(post, reComments);
        RetweetResponse retweet = transferRetweet(post.getRetweet());

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .isBlocked(post.isBlocked())
                .user(transferUser(post.getMember()))
                .images(imageResponses)
                .likers(likers)
                .comments(comments)
                .retweet(retweet)
                .major(post.getMajor())
                .interest(post.getInterest())
                .build();
    }

    private RetweetResponse transferRetweet(Post retweet) {
        if(retweet == null){
            return null;
        }
        return RetweetResponse.builder()
                    .id(retweet.getId())
                    .content(retweet.getContent())
                    .commentResponses(transferCommentResponses(retweet,transferReCommentResponses(retweet)))
                    .createdAt(retweet.getCreatedAt())
                    .imageResponses(transferImageResponses(retweet))
                    .interest(retweet.getInterest())
                    .isBlocked(retweet.isBlocked())
                    .likers(transferLikeResponses(retweet))
                    .major(retweet.getMajor())
                    .updatedAt(retweet.getUpdatedAt())
                    .title(retweet.getTitle())
                    .user(transferUser(retweet.getMember()))
                    .build();
    }

    private List<ImageResponse> transferImageResponses(Post post) {
        if(post.getImages().size()==0){
            return null;
        }
        List<ImageResponse> imageResponses = new ArrayList<>();
        post.getImages().forEach(v-> imageResponses.add(transferImage(v)));
        return imageResponses;
    }

    private List<LikeResponse> transferLikeResponses(Post post) {
        if(post.getLikes().size()==0){
            return null;
        }
        List<LikeResponse> likers = new ArrayList<>();
        post.getLikes().forEach(v-> likers.add(new LikeResponse(v.getId())));
        return likers;
    }

    private List<CommentResponse> transferCommentResponses(Post post, List<ReCommentResponse> reComments) {
        if(post.getComments().size()==0){
            return null;
        }
        List<CommentResponse> comments = new ArrayList<>();
        post.getComments().forEach(v->comments.add(new CommentResponse(
                v.getId(),
                v.getContent(),
                v.getCreatedAt(),
                v.getUpdatedAt(),
                reComments,
                transferUser(v.getMember())
        )));
        return comments;
    }

    private List<ReCommentResponse> transferReCommentResponses(Post post) {
        List<ReCommentResponse> reComments = new ArrayList<>();
        post.getComments().forEach(v->v.getChild().forEach(r->reComments.add(new ReCommentResponse(
                v.getId(),
                v.getContent(),
                v.getCreatedAt(),
                v.getUpdatedAt(),
                transferUser(v.getMember())
        ))));
        if(reComments.size()==0){
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
        if(image == null){
            return null;
        }
        return new ImageResponse(
                image.getSrc()
        );
    }


    //글 내용에 해쉬태그가 있는 부분들을 리스트에 저장에 반환한다.
    public List<String> getHashtags(String content){
        Pattern pattern = Pattern.compile("(?<!&)#(\\w+|[가-힣\\x{2712}\\x{2714}\\x{2716}\\x{271d}\\x{2721}\\x{2728}\\x{2733}\\x{2734}\\x{2744}\\x{2747}\\x{274c}\\x{274e}\\x{2753}-\\x{2755}\\x{2757}\\x{2763}\\x{2764}\\x{2795}-\\x{2797}\\x{27a1}\\x{27b0}\\x{27bf}\\x{2934}\\x{2935}\\x{2b05}-\\x{2b07}\\x{2b1b}\\x{2b1c}\\x{2b50}\\x{2b55}\\x{3030}\\x{303d}\\x{1f004}\\x{1f0cf}\\x{1f170}\\x{1f171}\\x{1f17e}\\x{1f17f}\\x{1f18e}\\x{1f191}-\\x{1f19a}\\x{1f201}\\x{1f202}\\x{1f21a}\\x{1f22f}\\x{1f232}-\\x{1f23a}\\x{1f250}\\x{1f251}\\x{1f300}-\\x{1f321}\\x{1f324}-\\x{1f393}\\x{1f396}\\x{1f397}\\x{1f399}-\\x{1f39b}\\x{1f39e}-\\x{1f3f0}\\x{1f3f3}-\\x{1f3f5}\\x{1f3f7}-\\x{1f4fd}\\x{1f4ff}-\\x{1f53d}\\x{1f549}-\\x{1f54e}\\x{1f550}-\\x{1f567}\\x{1f56f}\\x{1f570}\\x{1f573}-\\x{1f579}\\x{1f587}\\x{1f58a}-\\x{1f58d}\\x{1f590}\\x{1f595}\\x{1f596}\\x{1f5a5}\\x{1f5a8}\\x{1f5b1}\\x{1f5b2}\\x{1f5bc}\\x{1f5c2}-\\x{1f5c4}\\x{1f5d1}-\\x{1f5d3}\\x{1f5dc}-\\x{1f5de}\\x{1f5e1}\\x{1f5e3}\\x{1f5ef}\\x{1f5f3}\\x{1f5fa}-\\x{1f64f}\\x{1f680}-\\x{1f6c5}\\x{1f6cb}-\\x{1f6d0}\\x{1f6e0}-\\x{1f6e5}\\x{1f6e9}\\x{1f6eb}\\x{1f6ec}\\x{1f6f0}\\x{1f6f3}\\x{1f910}-\\x{1f918}\\x{1f980}-\\x{1f984}\\x{1f9c0}\\x{3297}\\x{3299}\\x{a9}\\x{ae}\\x{203c}\\x{2049}\\x{2122}\\x{2139}\\x{2194}-\\x{2199}\\x{21a9}\\x{21aa}\\x{231a}\\x{231b}\\x{2328}\\x{2388}\\x{23cf}\\x{23e9}-\\x{23f3}\\x{23f8}-\\x{23fa}\\x{24c2}\\x{25aa}\\x{25ab}\\x{25b6}\\x{25c0}\\x{25fb}-\\x{25fe}\\x{2600}-\\x{2604}\\x{260e}\\x{2611}\\x{2614}\\x{2615}\\x{2618}\\x{261d}\\x{2620}\\x{2622}\\x{2623}\\x{2626}\\x{262a}\\x{262e}\\x{262f}\\x{2638}-\\x{263a}\\x{2648}-\\x{2653}\\x{2660}\\x{2663}\\x{2665}\\x{2666}\\x{2668}\\x{267b}\\x{267f}\\x{2692}-\\x{2694}\\x{2696}\\x{2697}\\x{2699}\\x{269b}\\x{269c}\\x{26a0}\\x{26a1}\\x{26aa}\\x{26ab}\\x{26b0}\\x{26b1}\\x{26bd}\\x{26be}\\x{26c4}\\x{26c5}\\x{26c8}\\x{26ce}\\x{26cf}\\x{26d1}\\x{26d3}\\x{26d4}\\x{26e9}\\x{26ea}\\x{26f0}-\\x{26f5}\\x{26f7}-\\x{26fa}\\x{26fd}\\x{2702}\\x{2705}\\x{2708}-\\x{270d}\\x{270f}]|\\x{23}\\x{20e3}|\\x{2a}\\x{20e3}|\\x{30}\\x{20e3}|\\x{31}\\x{20e3}|\\x{32}\\x{20e3}|\\x{33}\\x{20e3}|\\x{34}\\x{20e3}|\\x{35}\\x{20e3}|\\x{36}\\x{20e3}|\\x{37}\\x{20e3}|\\x{38}\\x{20e3}|\\x{39}\\x{20e3}|\\x{1f1e6}[\\x{1f1e8}-\\x{1f1ec}\\x{1f1ee}\\x{1f1f1}\\x{1f1f2}\\x{1f1f4}\\x{1f1f6}-\\x{1f1fa}\\x{1f1fc}\\x{1f1fd}\\x{1f1ff}]|\\x{1f1e7}[\\x{1f1e6}\\x{1f1e7}\\x{1f1e9}-\\x{1f1ef}\\x{1f1f1}-\\x{1f1f4}\\x{1f1f6}-\\x{1f1f9}\\x{1f1fb}\\x{1f1fc}\\x{1f1fe}\\x{1f1ff}]|\\x{1f1e8}[\\x{1f1e6}\\x{1f1e8}\\x{1f1e9}\\x{1f1eb}-\\x{1f1ee}\\x{1f1f0}-\\x{1f1f5}\\x{1f1f7}\\x{1f1fa}-\\x{1f1ff}]|\\x{1f1e9}[\\x{1f1ea}\\x{1f1ec}\\x{1f1ef}\\x{1f1f0}\\x{1f1f2}\\x{1f1f4}\\x{1f1ff}]|\\x{1f1ea}[\\x{1f1e6}\\x{1f1e8}\\x{1f1ea}\\x{1f1ec}\\x{1f1ed}\\x{1f1f7}-\\x{1f1fa}]|\\x{1f1eb}[\\x{1f1ee}-\\x{1f1f0}\\x{1f1f2}\\x{1f1f4}\\x{1f1f7}]|\\x{1f1ec}[\\x{1f1e6}\\x{1f1e7}\\x{1f1e9}-\\x{1f1ee}\\x{1f1f1}-\\x{1f1f3}\\x{1f1f5}-\\x{1f1fa}\\x{1f1fc}\\x{1f1fe}]|\\x{1f1ed}[\\x{1f1f0}\\x{1f1f2}\\x{1f1f3}\\x{1f1f7}\\x{1f1f9}\\x{1f1fa}]|\\x{1f1ee}[\\x{1f1e8}-\\x{1f1ea}\\x{1f1f1}-\\x{1f1f4}\\x{1f1f6}-\\x{1f1f9}]|\\x{1f1ef}[\\x{1f1ea}\\x{1f1f2}\\x{1f1f4}\\x{1f1f5}]|\\x{1f1f0}[\\x{1f1ea}\\x{1f1ec}-\\x{1f1ee}\\x{1f1f2}\\x{1f1f3}\\x{1f1f5}\\x{1f1f7}\\x{1f1fc}\\x{1f1fe}\\x{1f1ff}]|\\x{1f1f1}[\\x{1f1e6}-\\x{1f1e8}\\x{1f1ee}\\x{1f1f0}\\x{1f1f7}-\\x{1f1fb}\\x{1f1fe}]|\\x{1f1f2}[\\x{1f1e6}\\x{1f1e8}-\\x{1f1ed}\\x{1f1f0}-\\x{1f1ff}]|\\x{1f1f3}[\\x{1f1e6}\\x{1f1e8}\\x{1f1ea}-\\x{1f1ec}\\x{1f1ee}\\x{1f1f1}\\x{1f1f4}\\x{1f1f5}\\x{1f1f7}\\x{1f1fa}\\x{1f1ff}]|\\x{1f1f4}\\x{1f1f2}|\\x{1f1f5}[\\x{1f1e6}\\x{1f1ea}-\\x{1f1ed}\\x{1f1f0}-\\x{1f1f3}\\x{1f1f7}-\\x{1f1f9}\\x{1f1fc}\\x{1f1fe}]|\\x{1f1f6}\\x{1f1e6}|\\x{1f1f7}[\\x{1f1ea}\\x{1f1f4}\\x{1f1f8}\\x{1f1fa}\\x{1f1fc}]|\\x{1f1f8}[\\x{1f1e6}-\\x{1f1ea}\\x{1f1ec}-\\x{1f1f4}\\x{1f1f7}-\\x{1f1f9}\\x{1f1fb}\\x{1f1fd}-\\x{1f1ff}]|\\x{1f1f9}[\\x{1f1e6}\\x{1f1e8}\\x{1f1e9}\\x{1f1eb}-\\x{1f1ed}\\x{1f1ef}-\\x{1f1f4}\\x{1f1f7}\\x{1f1f9}\\x{1f1fb}\\x{1f1fc}\\x{1f1ff}]|\\x{1f1fa}[\\x{1f1e6}\\x{1f1ec}\\x{1f1f2}\\x{1f1f8}\\x{1f1fe}\\x{1f1ff}]|\\x{1f1fb}[\\x{1f1e6}\\x{1f1e8}\\x{1f1ea}\\x{1f1ec}\\x{1f1ee}\\x{1f1f3}\\x{1f1fa}]|\\x{1f1fc}[\\x{1f1eb}\\x{1f1f8}]|\\x{1f1fd}\\x{1f1f0}|\\x{1f1fe}[\\x{1f1ea}\\x{1f1f9}]|\\x{1f1ff}[\\x{1f1e6}\\x{1f1f2}\\x{1f1fc}])+");
        Matcher matcher = pattern.matcher(content);

        List<String> hashtags = new ArrayList<>();
        while(matcher.find()){
            hashtags.add(matcher.group());
        }
        return hashtags;
    }

    //해당 유저가 구독한 게시판의 글들을 가져온다.
    public List<Post> getPreferencePosts(Long id){
        Member member = memberRepository.findMemberById(id);
        List<String> majors = member.getMajors().stream().map(Major::getMajor).collect(Collectors.toList());
        PostSearchCondition condition =
                PostSearchCondition.builder()
                .majors(majors)
                .build();
        postRepository.search(condition);

        return null;
    }

}
