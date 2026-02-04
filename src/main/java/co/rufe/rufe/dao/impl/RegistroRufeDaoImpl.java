package co.rufe.rufe.dao.impl;

import co.rufe.rufe.dao.IRegistroRufeDao;
import co.rufe.rufe.model.ActivoAgropecuario;
import co.rufe.rufe.model.BienAfectado;
import co.rufe.rufe.model.IntegranteHogar;
import co.rufe.rufe.model.RegistroRufe;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class RegistroRufeDaoImpl implements IRegistroRufeDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public RegistroRufeDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public RegistroRufe save(RegistroRufe registro) {
        String sql = "INSERT INTO registros_rufe (organizacion_id, evento_id, tipo_evento_id, usuario_registrador_id, cliente_id, "
                +
                "fecha_registro, tipo_ubicacion_bien_id, corregimiento, vereda_sector_barrio, direccion, " +
                "tipo_alojamiento_actual_id, lugar_habitual_residencia, evacuado_fuera_residencia, " +
                "observaciones, vo_bo_cmgrd, fecha_creacion, fecha_actualizacion) " +
                "VALUES (:organizacionId, :eventoId, :tipoEventoId, :usuarioRegistradorId, :clienteId, " +
                ":fechaRegistro, :tipoUbicacionBienId, :corregimiento, :veredaSectorBarrio, :direccion, " +
                ":tipoAlojamientoActualId, :lugarHabitualResidencia, :evacuadoFueraResidencia, " +
                ":observaciones, :voBoCmgrd, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizacionId", registro.getOrganizacionId());
        params.addValue("eventoId", registro.getEventoId());
        params.addValue("tipoEventoId", registro.getTipoEventoId());
        params.addValue("usuarioRegistradorId", registro.getUsuarioRegistradorId());
        params.addValue("clienteId", registro.getClienteId());
        params.addValue("fechaRegistro", registro.getFechaRegistro());
        params.addValue("tipoUbicacionBienId", registro.getTipoUbicacionBienId());
        params.addValue("corregimiento", registro.getCorregimiento());
        params.addValue("veredaSectorBarrio", registro.getVeredaSectorBarrio());
        params.addValue("direccion", registro.getDireccion());
        params.addValue("tipoAlojamientoActualId", registro.getTipoAlojamientoActualId());
        params.addValue("lugarHabitualResidencia", registro.getLugarHabitualResidencia());
        params.addValue("evacuadoFueraResidencia", registro.getEvacuadoFueraResidencia());
        params.addValue("observaciones", registro.getObservaciones());
        params.addValue("voBoCmgrd", registro.getVoBoCmgrd());

        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        registro.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return registro;
    }

    @Override
    public void saveIntegrante(IntegranteHogar integrante) {
        String sql = "INSERT INTO integrantes_hogar (registro_rufe_id, cliente_id, registro_rufe_cliente_id, " +
                "nombres, apellidos, tipo_documento_id, numero_documento, fecha_nacimiento, " +
                "parentesco_id, genero_id, pertenencia_etnica_id, telefono, fecha_creacion, fecha_actualizacion) " +
                "VALUES (:registroRufeId, :clienteId, :registroRufeClienteId, " +
                ":nombres, :apellidos, :tipoDocumentoId, :numeroDocumento, :fechaNacimiento, " +
                ":parentescoId, :generoId, :pertenenciaEtnicaId, :telefono, NOW(), NOW())";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("registroRufeId", integrante.getRegistroRufeId());
        params.addValue("clienteId", integrante.getClienteId());
        params.addValue("registroRufeClienteId", integrante.getRegistroRufeClienteId());
        params.addValue("nombres", integrante.getNombres());
        params.addValue("apellidos", integrante.getApellidos());
        params.addValue("tipoDocumentoId", integrante.getTipoDocumentoId());
        params.addValue("numeroDocumento", integrante.getNumeroDocumento());
        params.addValue("fechaNacimiento", integrante.getFechaNacimiento());
        params.addValue("parentescoId", integrante.getParentescoId());
        params.addValue("generoId", integrante.getGeneroId());
        params.addValue("pertenenciaEtnicaId", integrante.getPertenenciaEtnicaId());
        params.addValue("telefono", integrante.getTelefono());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void saveBienAfectado(BienAfectado bien) {
        String sql = "INSERT INTO bienes_afectados (registro_rufe_id, cliente_id, registro_rufe_cliente_id, " +
                "tipo_bien_id, forma_tenencia_bien_id, estado_bien_id, fecha_creacion, fecha_actualizacion) " +
                "VALUES (:registroRufeId, :clienteId, :registroRufeClienteId, " +
                ":tipoBienId, :formaTenenciaBienId, :estadoBienId, NOW(), NOW())";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("registroRufeId", bien.getRegistroRufeId());
        params.addValue("clienteId", bien.getClienteId());
        params.addValue("registroRufeClienteId", bien.getRegistroRufeClienteId());
        params.addValue("tipoBienId", bien.getTipoBienId());
        params.addValue("formaTenenciaBienId", bien.getFormaTenenciaBienId());
        params.addValue("estadoBienId", bien.getEstadoBienId());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void saveActivoAgropecuario(ActivoAgropecuario activo) {
        String sql = "INSERT INTO activos_agropecuarios (registro_rufe_id, cliente_id, registro_rufe_cliente_id, " +
                "sector, tipo_cultivo, unidad_medida_agricola, area_cantidad_agricola, " +
                "especie_animal, cantidad_animal, fecha_creacion, fecha_actualizacion) " +
                "VALUES (:registroRufeId, :clienteId, :registroRufeClienteId, :sector, " +
                ":tipoCultivo, :unidadMedidaAgricola, :areaCantidadAgricola, " +
                ":especieAnimal, :cantidadAnimal, NOW(), NOW())";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("registroRufeId", activo.getRegistroRufeId());
        params.addValue("clienteId", activo.getClienteId());
        params.addValue("registroRufeClienteId", activo.getRegistroRufeClienteId());
        params.addValue("sector", activo.getSector());
        params.addValue("tipoCultivo", activo.getTipoCultivo());
        params.addValue("unidadMedidaAgricola", activo.getUnidadMedidaAgricola());
        params.addValue("areaCantidadAgricola", activo.getAreaCantidadAgricola());
        params.addValue("especieAnimal", activo.getEspecieAnimal());
        params.addValue("cantidadAnimal", activo.getCantidadAnimal());

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public boolean existsByClienteIdAndOrganizacionId(String clienteId, Long organizacionId) {
        String sql = "SELECT COUNT(*) FROM registros_rufe WHERE cliente_id = :clienteId AND organizacion_id = :organizacionId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clienteId", clienteId);
        params.addValue("organizacionId", organizacionId);

        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByNumeroDocumentoAndEventoId(String numeroDocumento, Long eventoId) {
        // Updated to remove 'activo' checks if not present in SQL, assuming
        // 'fecha_eliminacion IS NULL' logically
        String sql = "SELECT COUNT(*) FROM integrantes_hogar i " +
                "JOIN registros_rufe r ON i.registro_rufe_id = r.id " +
                "WHERE i.numero_documento = :numeroDocumento " +
                "AND r.evento_id = :eventoId " +
                "AND r.fecha_eliminacion IS NULL AND i.fecha_eliminacion IS NULL";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("numeroDocumento", numeroDocumento);
        params.addValue("eventoId", eventoId);

        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public Optional<RegistroRufe> findById(Long id) {
        return Optional.empty(); // Not implemented yet
    }
}
