package dam.tfg.pokeplace.models;

public class Pokemon {
    private String pokedexNumber;
    private String name;
    private String sprite;

    public Pokemon(){

    }
    public Pokemon(String pokedexNumber, String name, String sprite) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.sprite = sprite;
    }

    public String getPokedexNumber() {
        return pokedexNumber;
    }

    public void setPokedexNumber(String pokedexNumber) {
        this.pokedexNumber = pokedexNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }
}
