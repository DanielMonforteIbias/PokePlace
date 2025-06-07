package dam.tfg.pokeplace.data;

public enum DamageMultiplier {
    NO_EFFECT(0),
    NOT_EFFECTIVE(0.5),
    NEUTRAL(1),
    VERY_EFFECTIVE(2);
    public final double multiplier;

    //ENUMS CONSTRUCTORS MUST BE PRIVATE
    private DamageMultiplier(double multiplier) {
        this.multiplier=multiplier;
    }
}
