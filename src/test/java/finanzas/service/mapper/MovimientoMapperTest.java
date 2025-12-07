package finanzas.service.mapper;

import static finanzas.domain.MovimientoAsserts.*;
import static finanzas.domain.MovimientoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovimientoMapperTest {

    private MovimientoMapper movimientoMapper;

    @BeforeEach
    void setUp() {
        movimientoMapper = new MovimientoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMovimientoSample1();
        var actual = movimientoMapper.toEntity(movimientoMapper.toDto(expected));
        assertMovimientoAllPropertiesEquals(expected, actual);
    }
}
