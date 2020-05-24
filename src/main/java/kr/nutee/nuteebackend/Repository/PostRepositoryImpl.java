//package kr.nutee.nuteebackend.Repository;
//
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import kr.nutee.nuteebackend.DTO.PostSearchCondition;
//import kr.nutee.nuteebackend.Domain.Post;
//import kr.nutee.nuteebackend.Domain.QPost;
//
//import static kr.nutee.nuteebackend.Domain.QPost.post;
//import static kr.nutee.nuteebackend.Domain.QMember.member;
//
//import javax.persistence.EntityManager;
//import java.util.List;
//
//import static org.springframework.util.StringUtils.hasText;
//import static org.springframework.util.StringUtils.isEmpty;
//
//public class PostRepositoryImpl implements PostRepositoryCustom {
//
//    private final JPAQueryFactory queryFactory;
//
//    public PostRepositoryImpl(EntityManager em) {
//        this.queryFactory = new JPAQueryFactory(em);
//    }
//
//    @Override
//    public List<Post> search(PostSearchCondition condition) {
//        return queryFactory
//                .select(new QPost(post))
//                .from(post)
//                .leftJoin(post.member,member)
//                .where(
//                        interestEq(condition.getInterests().get(0))
//                )
//                .fetch();
//    }
//
//    private BooleanExpression interestEq(String interest){
//        return isEmpty(interest) ? null : post.interest.eq(interest);
//    }
//
//    private BooleanExpression writerNumEq(Long writerNum){
//        return writerNum == null ? null : post.member.id.eq(writerNum);
//    }
//
//    private BooleanExpression majorEq(String major){
//        return isEmpty(major) ? null : post.major.eq(major);
//    }
//
//    private BooleanExpression contentEq(String content){
//        return hasText(content) ? null : post.content.contains(content);
//    }
//}
