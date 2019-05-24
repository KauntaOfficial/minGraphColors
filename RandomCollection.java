import java.util.Random;
import java.util.TreeMap;
import java.util.NavigableMap;

public class RandomCollection<E> implements Cloneable
{
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;
    
    public RandomCollection() 
    {
        this(new Random());
    }
    
    public RandomCollection(Random random)
    {
        this.random = random;
    }
    
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
    public RandomCollection<E> add(double weight, E result) 
    {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E next() 
    {
        double value = random.nextDouble() * total;
        E returnedValue = map.higherEntry(value).getValue();
        map.remove(returnedValue);
        return returnedValue;
    }
    
}
