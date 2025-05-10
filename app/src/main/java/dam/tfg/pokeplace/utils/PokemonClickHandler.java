package dam.tfg.pokeplace.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.util.List;

import dam.tfg.pokeplace.PokemonDetailsActivity;
import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.api.PokeApiDetailsResponse;
import dam.tfg.pokeplace.api.PokemonCallback;
import dam.tfg.pokeplace.api.PokemonSpeciesCallback;
import dam.tfg.pokeplace.models.BasePokemon;
import dam.tfg.pokeplace.models.Move;
import dam.tfg.pokeplace.models.Pokemon;

public class PokemonClickHandler {
    public static void handlePokemonClick(Context context, BasePokemon pokemon){
        if(pokemon!=null){
            if(pokemon.getUrl()!=null)fetchPokemon(pokemon,context);
            else ToastUtil.showToast(context,context.getString(R.string.error_pokemon_not_available));
        }
    }
    private static void fetchPokemon(BasePokemon pokemon, Context context) {
        String url=pokemon.getUrl();
        PokeApiDetailsResponse.getPokemon(url, new PokemonCallback() {
            @Override
            public void onPokemonReceived(Pokemon pokemon) {
                String urlSpecies=url.replace("pokemon","pokemon-species"); //Creamos la URL para conseguir la especie
                fetchDescriptions(pokemon,urlSpecies,context);
            }
            @Override
            public void onMoveReceived(Move move) {

            }
        },context);
    }
    private static void fetchDescriptions(Pokemon pokemon, String url, Context context){
        PokeApiDetailsResponse.getPokemonDetails(url, new PokemonSpeciesCallback() {
            @Override
            public void onDetailsReceived(List<Pair<String, String>> descriptions) {
                pokemon.setDescriptions(descriptions);
                launchPokemonDetailsActivity(context,pokemon);
            }

        },context);
    }

    private static void launchPokemonDetailsActivity(Context context, Pokemon pokemon){
        Intent intent=new Intent(context, PokemonDetailsActivity.class);
        intent.putExtra("Pokemon",pokemon);
        context.startActivity(intent);
    }
}
