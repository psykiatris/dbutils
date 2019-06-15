package org.palczewski.proposed;
/*
Learning to create a builder for the class

Taken from Joshua Bloch's "Effective Java". The idea behind this builder
 class is to create an immutable (unchanging) object, while allowing
 customized object construction with many optional parameters. So, here,
  two variables are required, while the rest are optional, allowing the
  calling program to only set the variables it needs.
 */
public class NutritionFacts {

    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrates;

    public static class Builder {

        // Required parameters
        private final int servingSize;
        private final int servings;

        // Optional params with default values
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrates = 0;

        public Builder(int servingSize, int servings) {

            this.servingSize = servingSize;
            this.servings = servings;

        }

        // Optional fields set
        public Builder calories(int val) {
            calories = val;
            return this;
        }
        public Builder fat(int val) {
            fat = val;
            return this;
        }
        public Builder sodium(int val) {
            sodium = val;
            return this;
        }
        public Builder carbohydrates(int val) {
            carbohydrates = val;
            return this;
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutritionFacts(Builder builder) {

        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrates = builder.carbohydrates;

    }

    public static void main(String[] args) {

        NutritionFacts cocaCola =
                new Builder(240, 8).calories(100).sodium(68).carbohydrates(27).build();

    }

}
