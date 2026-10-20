interface Pizza{
    String getDescription();
    double getCost();
}
class PlainPizza implements Pizza {
    @Override
    public String getDescription() {
        return "Plain Dough & Sauce";
    }

    @Override
    public double getCost() {
        return 5.0;
    }
}
abstract class ToppingDecorator implements Pizza {
    protected final Pizza pizza;
    public ToppingDecorator(Pizza pizza) {
        this.pizza = pizza;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription();
    }

    @Override
    public double getCost() {
        return pizza.getCost();
    }
}

class Cheese extends ToppingDecorator {
    public Cheese(Pizza pizza) { super(pizza); }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Cheese";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 2.0;
    }
}

class Olives extends ToppingDecorator {
    public Olives(Pizza pizza) { super(pizza); }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Olives";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 1.0;
    }
}

class Pineapple extends ToppingDecorator {
    public Pineapple(Pizza pizza) { super(pizza); }

    @Override
    public String getDescription() {
        return pizza.getDescription() + " + Pineapple";
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 1.5;
    }
}

public class did {
    public static void main(String[] args) {
        Pizza myPizza=new Pineapple(new Olives(new Cheese(new PlainPizza())));
        System.out.println("Description : " + myPizza.getDescription());
        System.out.printf ("Total cost  : $%.2f%n", myPizza.getCost());

        System.out.println();
        Pizza other = new Cheese(new Pineapple(new Olives(new PlainPizza())));
        System.out.println("Description : " + other.getDescription());
        System.out.printf ("Total cost  : $%.2f%n", other.getCost());

        System.out.println();
        Pizza doubleCheese = new Cheese(new Cheese(new PlainPizza()));
        System.out.println("Description : " + doubleCheese.getDescription());
        System.out.printf ("Total cost  : $%.2f%n", doubleCheese.getCost());
    }
}