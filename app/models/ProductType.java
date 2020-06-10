package models;

/**
 * @author hendriksaragih
 */
public class ProductType {
    public static ProductType productTypeOwn = new ProductType(1, "Own Product");
    public static ProductType productTypeConsignment = new ProductType(2, "Consignment");
    public static ProductType productTypeMarketplace = new ProductType(3, "Marketplace");

    private int id;
    private String name;

    public ProductType(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ProductType[] getProductTypeList(){
        return new ProductType[]
                {ProductType.productTypeOwn, ProductType.productTypeConsignment, ProductType.productTypeMarketplace};
    }

    public static ProductType getProductTypeById(int id){
        ProductType result = null;
        ProductType[] listType = getProductTypeList();
        for(ProductType type:listType){
            if(type.id == id){
                result = type;
            }
        }
        return result;
    }
}
