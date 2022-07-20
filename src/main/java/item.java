public class item {
    private String id;
    private String parrent;
    private String name;
    private double price;

    public item(String id, String parrent, String name, double price) {
        this.id = id;
        this.parrent = parrent;
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "item{" +
                "id='" + id + '\'' +
                ", parrent='" + parrent + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParrent() {
        return parrent;
    }

    public void setParrent(String parrent) {
        this.parrent = parrent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
