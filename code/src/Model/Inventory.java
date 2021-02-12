package Model;
import javafx.collections.ObservableList;


public class Inventory
{
    private static ObservableList<Part> allParts;
    private static ObservableList<Product> allProducts;

    public Inventory()
    {
        allParts = javafx.collections.FXCollections.observableArrayList();
        allProducts = javafx.collections.FXCollections.observableArrayList();
    }

    public static void addPart(Part newPart) { allParts.add(newPart);}
    public static void addProduct(Product newProduct) { allProducts.add(newProduct);}

    public Part lookupPart(int partId)
    {
        for (int i = 0; i < allParts.size(); i++)
        {
            Part part = allParts.get(i);
            if (partId == part.getId()) { return part;}
        }
        return null;
    }

    public ObservableList<Part> lookupPart(String partName)
    {
        ObservableList<Part> partsList = javafx.collections.FXCollections.observableArrayList();
        for (int i = 0; i < allParts.size(); i++)
        {
            Part part = allParts.get(i);
            if (part.getName().contains(partName)) { partsList.add(part);}
        }
        return partsList;
    }

    public Product lookupProduct(int productId)
    {
        for(int i=0; i<allProducts.size(); i++)
        {
            Product product = allProducts.get(i);
            if (productId == product.getId()) { return product;}
        }
        return null;
    }

    public ObservableList<Product> lookupProduct(String productName)
    {
        ObservableList<Product> productsList = javafx.collections.FXCollections.observableArrayList();
        for (int i = 0; i < allProducts.size(); i++)
        {
            Product product = allProducts.get(i);
            if (product.getName().contains(productName)) { productsList.add(product);}
        }
        return productsList;
    }

    public void updatePart(int index, Part newPart)
    {
        allParts.set(index, newPart);
    }
    public void updateProduct(int index, Product newProduct)
    {
        allProducts.set(index, newProduct);
    }

    public boolean deletePart(Part selectedPart)
    {
        for (int i = 0; i < allParts.size(); i++)
        {
            if (allParts.get(i) == selectedPart) {
                allParts.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean deleteProduct(Product selectedProduct)
    {
        for (int i = 0; i < allProducts.size(); i++)
        {
            if (allProducts.get(i) == selectedProduct) {
                allProducts.remove(i);
                return true;
            }
        }
        return false;
    }

    public static ObservableList<Part> getAllParts() { return allParts;}
    public static ObservableList<Product> getAllProducts() { return allProducts;}

}
