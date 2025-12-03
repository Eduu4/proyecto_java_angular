package finanzas.service.mapper;

import finanzas.domain.Categoria;
import finanzas.domain.Cuenta;
import finanzas.domain.Movimiento;
import finanzas.domain.User;
import finanzas.service.dto.CategoriaDTO;
import finanzas.service.dto.CuentaDTO;
import finanzas.service.dto.MovimientoDTO;
import finanzas.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Movimiento} and its DTO {@link MovimientoDTO}.
 */
@Mapper(componentModel = "spring")
public interface MovimientoMapper extends EntityMapper<MovimientoDTO, Movimiento> {
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "userLogin")
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoriaId")
    @Mapping(target = "cuenta", source = "cuenta", qualifiedByName = "cuentaId")
    MovimientoDTO toDto(Movimiento s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("categoriaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoriaDTO toDtoCategoriaId(Categoria categoria);

    @Named("cuentaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CuentaDTO toDtoCuentaId(Cuenta cuenta);
}
