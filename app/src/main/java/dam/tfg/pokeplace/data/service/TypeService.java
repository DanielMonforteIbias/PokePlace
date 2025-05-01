package dam.tfg.pokeplace.data.service;

import java.util.List;

import dam.tfg.pokeplace.data.dao.TypeDAO;
import dam.tfg.pokeplace.data.dao.TypeRelationDAO;
import dam.tfg.pokeplace.models.Type;

public class TypeService {
    private TypeDAO typeDAO;
    private TypeRelationDAO typeRelationDAO;

    public TypeService(TypeDAO typeDAO, TypeRelationDAO typeRelationDAO) {
        this.typeDAO = typeDAO;
        this.typeRelationDAO = typeRelationDAO;
    }
    public void addAllTypes(List<Type> types) {
        //Primero añadimos los tipos, luego sus relaciones, para evitar problemas de FKs
        for (Type type : types) {
            typeDAO.addType(type);
        }
        for (Type type : types) {
            addRelations(type);
        }
    }
    private void addRelations(Type type) {
        for (String target:type.getDoubleDamageTo()) {
            typeRelationDAO.addTypeRelation(type.getName(), target, "double_damage_to");
        }
        for (String target:type.getDoubleDamageFrom()) {
            typeRelationDAO.addTypeRelation(type.getName(), target, "double_damage_from");
        }
        for (String target:type.getHalfDamageTo()) {
            typeRelationDAO.addTypeRelation(type.getName(), target, "half_damage_to");
        }
        for (String target:type.getHalfDamageFrom()) {
            typeRelationDAO.addTypeRelation(type.getName(), target, "half_damage_from");
        }
        for (String target:type.getNoDamageTo()) {
            typeRelationDAO.addTypeRelation(type.getName(),target,"no_damage_to");
        }
        for (String target:type.getNoDamageFrom()) {
            typeRelationDAO.addTypeRelation(type.getName(), target, "no_damage_from");
        }
    }
    public Type getType(String typeName) {
        Type type = typeDAO.getType(typeName);
        if (type != null) typeRelationDAO.loadRelationsForType(type);
        return type;
    }

    public List<Type> getAllTypes() {
        List<Type> types = typeDAO.getTypes();
        for (Type type : types) {
            typeRelationDAO.loadRelationsForType(type);
        }
        return types;
    }
}
