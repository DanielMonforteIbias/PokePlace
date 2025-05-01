package dam.tfg.pokeplace.utils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import dam.tfg.pokeplace.R;
import dam.tfg.pokeplace.data.Data;
import dam.tfg.pokeplace.models.Type;

public class ViewUtils {
    public static void setPokemonTypeBackground(Context context, View view, String typeName, float cornerRadius, int strokeWidth) {
        /*Cogemos el color del tipo de colors.
        Se ha intentado obtener dinamicamente de varias formas, con la libreria Palette o manualmente recorriendo los bitmaps,
        pero como son imagenes pequeñas algunas tienen resultados incoherentes y no hay otro modo de que sea el color que queremos*/
        if(typeName!=null) {
            Type type= Data.getInstance().getTypeByName(typeName);
            int colorId=0,color=0;
            if(type!=null){
                colorId=context.getResources().getIdentifier(type.getName(), "color", context.getPackageName());
                color=(colorId!=0) ? ContextCompat.getColor(context, colorId) : ContextCompat.getColor(context, R.color.gray_500); //Si el colorId no es 0, cogemos ese color, si lo es cogemos un gris por defecto
            }
            else color=ContextCompat.getColor(context, R.color.gray_500);
            GradientDrawable background = new GradientDrawable();
            background.setColor(color);
            background.setCornerRadius(cornerRadius);
            background.setStroke(strokeWidth, ContextCompat.getColor(context,R.color.light_icons));
            view.setBackground(background);
        }
    }
}
