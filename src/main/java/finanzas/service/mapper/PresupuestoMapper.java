package finanzas.service.mapper;

import finanzas.domain.Categoria;
import finanzas.domain.Presupuesto;
import finanzas.domain.User;
import finanzas.service.dto.CategoriaDTO;
import finanzas.service.dto.PresupuestoDTO;
import finanzas.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Presupuesto} and its DTO {@link PresupuestoDTO}.
 */
@Mapper(componentModel = "spring")
public interface PresupuestoMapper extends EntityMapper<PresupuestoDTO, Presupuesto> {
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "userLogin")
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoriaId")
    PresupuestoDTO toDto(Presupuesto s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("categoriaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoriaDTO toDtoCategoriaId(Categoria categoria);
}
