
package jgraph.AdjList;

/**
 * When using vertex representations other than String (Built in) then
 * you need to implement Comparable. Comparable tells the
 * graph API how to compare your objects.
 * @author Richard DeSilvey
 */
public class City implements Comparable<City>{

    private String name;
    private float population; 
    
    public City(String name){
        this.name = name;
        this.population = -1;
    }
    
    public void setPopulation(float p){
        population = p;
    }
    
    public float getPopulation(){
        return population;
    }
    
    public String toString(){
        return name;
    }

    @Override
    public int compareTo(City o) {
        return name.compareTo(o.name);
    }

}
