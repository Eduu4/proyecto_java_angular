package finanzas.service.mapper;

import finanzas.domain.Categoria;
import finanzas.domain.User;
import finanzas.service.dto.CategoriaDTO;
import finanzas.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Categoria} and its DTO {@link CategoriaDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoriaMapper extends EntityMapper<CategoriaDTO, Categoria> {
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "userLogin")
    CategoriaDTO toDto(Categoria s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
