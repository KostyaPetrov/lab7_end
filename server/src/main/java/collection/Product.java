package collection;


import java.time.LocalDate;


public class Product {
    private int id;
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int price; //Значение поля должно быть больше 0
    private String partNumber; //Длина строки должна быть не меньше 24, Строка не может быть пустой, Длина строки не должна быть больше 50, Поле не может быть null
    private Float manufactureCost; //Поле может быть null
    private UnitOfMeasure unitOfMeasure; //Поле не может быть null
    private Person owner; //Поле может быть null
    private String login;

    public Product(Integer id,String name, Coordinates coordinates, LocalDate creationDate, int price, String partNumber, Float manufactureCost, UnitOfMeasure unitOfMeasure, Person owner, String login) {
        this.id=id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.partNumber = partNumber;
        this.manufactureCost = manufactureCost;
        this.unitOfMeasure = unitOfMeasure;
        this.owner = owner;
        this.login=login;
    }

    @Override
    public String toString(){

            return String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s",id, name,coordinates, creationDate,price,partNumber,manufactureCost,unitOfMeasure,owner, login);


    }


    public Integer getId(){return id;}
    public String getName(){
        return name;
    }
    public Coordinates getCoordinates(){
        return coordinates;
    }

    public Integer getPrice(){
        return price;
    }
    public String getPartNumber(){return partNumber;}
    public LocalDate getCreationDate(){
        return creationDate;
    }
    public Float getManufactureCost(){return manufactureCost;}
    public UnitOfMeasure getUnitOfMeasure(){
        return unitOfMeasure;
    }
    public Person getOwner(){return owner;}
    public String getLogin(){return login;}
}
