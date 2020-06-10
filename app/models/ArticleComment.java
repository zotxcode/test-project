package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.CommonFunction;

import javax.persistence.*;
import java.util.List;

/**
 * @author hendriksaragih
 */
@Entity
public class ArticleComment extends BaseModel {

    public final static int APPROVED = 1;
    public final static int REJECT = 2;
    public final static int PENDING = 0;


    @JsonIgnore
    @JoinColumn(name = "comment_parent_id")
    @ManyToOne
    public ArticleComment replyFrom;

    @JsonIgnore
    @Column(insertable = false)
    @OneToMany(mappedBy = "replyFrom")
    public List<ArticleComment> replies;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Brand brand;

    @JsonIgnore
    @ManyToOne
    public Article article;

    @JsonProperty("article_id")
    public Long articleId() {
        return article.id;
    }

    @JsonProperty("article_title")
    public String articleTitle() {
        return article.title;
    }

    @JsonProperty("article_slug")
    public String articleSlug() {
        return article.slug;
    }

    @JsonProperty("created_at_str")
    public String getCreatedAtStr() {
        return CommonFunction.getDateTime(createdAt);
    }

    @JsonIgnore
    public Long commenterId;
    @JsonIgnore
    public boolean isAdmin;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    public String comment;

    public String name;
    public String email;
    public String website;

    @JsonIgnore
    public boolean isRemoved;

    public int status;

    @JoinColumn(name = "approve_by")
    @ManyToOne
    public UserCms userCms;

    @javax.persistence.Transient
    @JsonProperty("commenter_name")
    public String getCommenterNameBe() {
        String res = "";
        if (isAdmin) {
            UserCms userTarget = UserCms.find.byId(commenterId);
            res = "(Admin) "+userTarget.fullName+"<"+userTarget.email+">";
        }
        else{
            Member memberTarget = Member.find.byId(commenterId);
            res = memberTarget.fullName+"<"+memberTarget.email+">";
        }
        return res;
    }

    @JsonProperty("comment_text")
    public String getCommentText() {
        String res = "<This comment was removed.>";
        if (!isRemoved) {
            res = comment;
        }
        return res;
    }

    public boolean isParent(){
        return replyFrom == null;
    }
//
//    public ArticleComment(Article article, Long parentCommentId, String comment, Long commenterId, boolean isAdmin, Brand brand,
//                          String name, String email, String website) {
//        if (parentCommentId != null) {
//            this.replyFrom = ArticleComment.find.byId(parentCommentId);
//        }
//        this.article = article;
//        this.commenterId = commenterId;
//        this.isAdmin = isAdmin;
//        this.status = isAdmin ? APPROVED : PENDING;
//        this.comment = comment;
//        this.isRemoved = false;
//        this.brand = brand;
//        this.name = name;
//        this.email = email;
//        this.website = website;
//    }

    public ArticleComment(Article article, Long parentCommentId, String comment, Long commenterId, boolean isAdmin, Brand brand) {
        if (parentCommentId != null) {
            this.replyFrom = ArticleComment.find.byId(parentCommentId);
        }
        this.article = article;
        this.commenterId = commenterId;
        this.isAdmin = isAdmin;
        this.status = isAdmin ? APPROVED : PENDING;
        this.comment = comment;
        this.isRemoved = false;
        this.brand = brand;
    }

    public ArticleComment() {
    }

    public static Finder<Long, ArticleComment> find = new Finder<>(Long.class,
            ArticleComment.class);

    public static String validation(ArticleComment model) {
        if (model.replyFrom!=null && ArticleComment.find.byId(model.replyFrom.id) == null) {
            return "Comment is not found.";
        }
        if (model.comment.equals("")) {
            return "Please insert your comment.";
        }
        if (model.status!=APPROVED && model.status!=REJECT && model.status!=PENDING)
        {
            return "Please check input status.";
        }
        return null;
    }

    public String getStatusName(){
        if (!isRemoved){
            switch (status){
                case PENDING : return "Pending";
                case APPROVED : return "Approved";
                case REJECT : return "Rejected";
            }
        }
        return "Removed";
    }

    public static Page<ArticleComment> page(int page, int pageSize, String sortBy, String order, String name, int status, Brand brand) {
        ExpressionList<ArticleComment> qry = ArticleComment.find
                .where()
                .ilike("article.name", "%" + name + "%")
                .eq("t0.brand_id", brand.id)
                .eq("t0.is_deleted", false);

        if (status >= 0 && status != 3){
            qry.eq("status", status);
            qry.eq("isRemoved", false);
        }else if(status == 3) qry.eq("isRemoved", true);

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Integer RowCount(Brand brand) {
        return find.where().eq("is_deleted", false).eq("brand", brand).findRowCount();
    }

    public static Integer RowCount(Long id) {
        return find.where().eq("is_deleted", false)
                .eq("comment_parent_id", id).findRowCount();
    }

    public static ArticleComment findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

    public static List<ArticleComment> getComments(Long articleId, Long brandId){
        return find.where().eq("is_deleted", false)
                .eq("article_id", articleId).eq("status", APPROVED)
                .eq("comment_parent_id", null)
                .eq("brand_id", brandId)
                .setOrderBy("created_at DESC").findList();
    }

}