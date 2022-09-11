package collection;


import java.io.Serializable;

public class Coordinates implements Serializable {
    private long x; //Максимальное значение поля: 485
    private float y;


    public Coordinates(long x, float y) {
        this.x=x;
        this.y=y;

    }
    @Override
    public String toString(){
        return String.format("%s,%s",x,y);
    }
    public Long getCoordinateX(){
        return x;
    }
    public Float getCoordinateY(){
        return y;
    }
}