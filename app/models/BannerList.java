package models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hendriksaragih
 */
public class BannerList {
    private List<Product> products;
    private List<Category> categories1;
    private List<Category> categories2;
    private List<Category> categories3;

    public BannerList(){
        products = new ArrayList<>();
        categories1 = new ArrayList<>();
        categories2 = new ArrayList<>();
        categories3 = new ArrayList<>();
    }

    public BannerList(Banner data){
        if (data.products != null){
            products = data.products;
        }
        if (data.categories != null){
            categories2 = data.categories;
        }
    }

    public BannerList(BestSellerBanner data){
        if (data.products != null){
            products = data.products;
        }
        if (data.categories != null){
            categories2 = data.categories;
        }
    }

    public BannerList(CategoryBanner data){
        if (data.products != null){
            products = data.products;
        }
        if (data.categories != null){
            categories2 = data.categories;
        }
    }

    public BannerList(InstagramBanner data){
        if (data.products != null){
            products = data.products;
        }
        if (data.categories != null){
            categories2 = data.categories;
        }
    }

    public BannerList(NewArrivalBanner data){
        if (data.products != null){
            products = data.products;
        }
        if (data.categories != null){
            categories2 = data.categories;
        }
    }

    public BannerList(OnSaleBanner data){
        if (data.products != null){
            products = data.products;
        }
        if (data.categories != null){
            categories2 = data.categories;
        }
    }

    public BannerList(List<Product> data){
        if (data != null && !data.isEmpty()){
            products = data;
        }

    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Category> getCategories1() {
        return categories1;
    }

    public void setCategories1(List<Category> categories1) {
        this.categories1 = categories1;
    }

    public List<Category> getCategories2() {
        return categories2;
    }

    public void setCategories2(List<Category> categories2) {
        this.categories2 = categories2;
    }

    public List<Category> getCategories3() {
        return categories3;
    }

    public void setCategories3(List<Category> categories3) {
        this.categories3 = categories3;
    }
}