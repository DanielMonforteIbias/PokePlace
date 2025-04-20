package dam.tfg.pokeplace.models;

public class BasePokemon {
    private String pokedexNumber;
    private String name;
    private String sprite;
    private String url;
    private String type1;
    private String type2;
    public BasePokemon(){}
    public BasePokemon(String pokedexNumber, String name, String sprite, String url,String type1, String type2) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.sprite = sprite;
        this.url = url;
        this.type1=type1;
        this.type2=type2;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }
}
