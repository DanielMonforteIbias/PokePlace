package dam.tfg.pokeplace.interfaces;

import dam.tfg.pokeplace.models.Type;

public interface OnTypeSelectedListener {
    //Necesario tener una interfaz separada para mantener el comportamiento incluso al rotar
    public void onTypeSelected(Type type);
}
