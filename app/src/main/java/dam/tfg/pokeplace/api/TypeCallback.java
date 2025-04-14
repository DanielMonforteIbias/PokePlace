package dam.tfg.pokeplace.api;

import java.util.List;

import dam.tfg.pokeplace.models.Type;

public interface TypeCallback {
    public void onTypeListReceived(List<Type> typeList);
    public void onTypeReceived(Type type);
}
