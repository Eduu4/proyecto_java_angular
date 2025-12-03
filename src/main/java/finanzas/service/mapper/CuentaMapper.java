package finanzas.service.mapper;

import finanzas.domain.Cuenta;
import finanzas.domain.User;
import finanzas.service.dto.CuentaDTO;
import finanzas.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cuenta} and its DTO {@link CuentaDTO}.
 */
@Mapper(componentModel = "spring")
public interface CuentaMapper extends EntityMapper<CuentaDTO, Cuenta> {
    @Mapping(target = "usuario", source = "usuario", qualifiedByName = "userLogin")
    CuentaDTO toDto(Cuenta s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
