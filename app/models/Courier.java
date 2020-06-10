package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.enwie.util.Constant;

import javax.persistence.*;
import java.util.List;

@Entity
public class Courier extends BaseModel {

	private static final long serialVersionUID = 1L;

	public String name;
	public String code;
	@JsonProperty("image_name")
	@Column(name = "image_name", columnDefinition = "TEXT")
	public String imageName;
	@JsonProperty("image_keyword")
	public String imageKeyword;
	@JsonProperty("image_title")
	public String imageTitle;
	@JsonProperty("image_description")
	@Column(name = "image_description", columnDefinition = "TEXT")
	public String imageDescription;
	@JsonProperty("image_url")
	public String imageUrl;
	@ManyToOne(cascade = { CascadeType.ALL })
	public Brand brand;

	@Transient
	public String save;

	@javax.persistence.Transient
	public String imageLink;

	@javax.persistence.Transient
	public List<String> service;

	@javax.persistence.Transient
	public List<Long> detailId;

	@javax.persistence.Transient
	public Long townshipId;

	@javax.persistence.Transient
	public List<String> listname;

	@javax.persistence.Transient
	public List<String> listaddress;

	@javax.persistence.Transient
	public int imageUrlX;
	@javax.persistence.Transient
	public int imageUrlY;
	@javax.persistence.Transient
	public int imageUrlW;
	@javax.persistence.Transient
	public int imageUrlH;

	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;


	public String getImageUrl(){
		return getImageLink();
	}

	public String getImageLink(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	public static Finder<Long, Courier> find = new Finder<>(Long.class, Courier.class);

	public static Page<Courier> page(int page, int pageSize, String sortBy, String order, String name, Long brandId) {
		ExpressionList<Courier> qry = Courier.find
				.where()
				.eq("brand_id", brandId)
				.ilike("name", "%" + name + "%")
				.eq("is_deleted", false);

		return
				qry.orderBy(sortBy + " " + order)
						.findPagingList(pageSize)
						.setFetchAhead(false)
						.getPage(page);
	}

	public static int findRowCount(Long brandId) {
		return
				find.where()
						.eq("brand_id", brandId)
						.eq("is_deleted", false)
						.findRowCount();
	}

    public static Courier findById(Long id, Long brandId) {
        return find.where().eq("is_deleted", false).eq("id", id).eq("brand_id", brandId).findUnique();
    }

}
