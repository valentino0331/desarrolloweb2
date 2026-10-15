package pe.edu.utp.salviafinanzas.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.salviafinanzas.model.*;
import pe.edu.utp.salviafinanzas.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Inicializador de Base de Datos y Datos Semilla (Data Seeding)
 * EcoTechTrack — Trazabilidad y Fiscalización de RAEE (D.S. N° 009-2019-MINAM)
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingsMissionRepository missionRepository;
    private final AlmacenRepository almacenRepository;
    private final EmpresaGestoraRepository gestoraRepository;
    private final ResiduoRepository residuoRepository;
    private final HistorialMovimientoRepository historialRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        logger.info(">>> Iniciando configuración de roles y datos semilla de EcoTechTrack (RAEE - MINAM)...");

        // 1. Inicialización de Roles
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADMIN)));

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER)));

        Role guestRole = roleRepository.findByName(ERole.ROLE_GUEST)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_GUEST)));

        // 2. Usuario Administrador Ambiental (Jefe de TI / Fiscalizador)
        if (!userRepository.existsByUsername("admin")) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@ecotechtrack.utp.pe")
                    .password(passwordEncoder.encode("Admin123!*"))
                    .fullName("Administrador Ambiental y Fiscalizador TI")
                    .points(500)
                    .streak(15)
                    .monthlyIncome(new BigDecimal("4500.00"))
                    .roles(adminRoles)
                    .build();

            userRepository.save(admin);
            logger.info("Usuario 'admin' creado exitosamente (Clave: Admin123!*)");
        }

        // 3. Usuario Custodio / Encargado de Almacén (Valentino)
        User valentino;
        if (!userRepository.existsByUsername("valentino")) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);

            valentino = User.builder()
                    .username("valentino")
                    .email("valentino@ecotechtrack.utp.pe")
                    .password(passwordEncoder.encode("User123!*"))
                    .fullName("Valentino Martin Marca Quedena (Encargado Almacén)")
                    .points(250)
                    .streak(7)
                    .monthlyIncome(new BigDecimal("1800.00"))
                    .roles(userRoles)
                    .build();

            valentino = userRepository.save(valentino);
            logger.info("Usuario 'valentino' creado exitosamente (Clave: User123!*)");

            // 4. Categorías de Residuos RAEE
            Category catComida = categoryRepository.save(Category.builder()
                    .name("TIC - Informática")
                    .description("Equipos de Informática y Telecomunicaciones (D.S. 009-2019-MINAM)")
                    .monthlyBudget(new BigDecimal("450.00"))
                    .icon("computer")
                    .color("#2196F3")
                    .user(valentino)
                    .build());

            Category catUni = categoryRepository.save(Category.builder()
                    .name("Aparatos de Alumbrado")
                    .description("Lámparas fluorescentes con contenido de Mercurio")
                    .monthlyBudget(new BigDecimal("600.00"))
                    .icon("lightbulb")
                    .color("#FFC107")
                    .user(valentino)
                    .build());

            Category catTransp = categoryRepository.save(Category.builder()
                    .name("Pequeños Aparatos")
                    .description("Periféricos, routers, switches y fuentes de poder")
                    .monthlyBudget(new BigDecimal("150.00"))
                    .icon("devices_other")
                    .color("#4CAF50")
                    .user(valentino)
                    .build());

            // 5. Registros de Bajas de Activos RAEE
            expenseRepository.save(Expense.builder()
                    .amount(new BigDecimal("32.40"))
                    .categoryName("TIC - Informática")
                    .subcategory("Servidor Rack / Baterías Cadmio")
                    .description("Servidor Torre HP ProLiant ML350 Gen9 dado de baja técnica por obsolescencia")
                    .expenseDate(LocalDateTime.now().minusDays(1))
                    .recurrence("BAJA_DEFINITIVA")
                    .category(catComida)
                    .user(valentino)
                    .build());

            expenseRepository.save(Expense.builder()
                    .amount(new BigDecimal("21.30"))
                    .categoryName("TIC - Informática")
                    .subcategory("Impresora Láser")
                    .description("Impresora Láser HP LaserJet Enterprise M605 inoperativa con placas de plomo")
                    .expenseDate(LocalDateTime.now().minusDays(3))
                    .recurrence("BAJA_DEFINITIVA")
                    .category(catComida)
                    .user(valentino)
                    .build());

            expenseRepository.save(Expense.builder()
                    .amount(new BigDecimal("4.50"))
                    .categoryName("Pequeños Aparatos")
                    .subcategory("Switch de Red")
                    .description("Switch Cisco Catalyst 2960-X dado de baja por falla de tarjeta madre")
                    .expenseDate(LocalDateTime.now().minusDays(2))
                    .recurrence("BAJA_DEFINITIVA")
                    .category(catTransp)
                    .user(valentino)
                    .build());

            // 6. Manifiestos de Entrega a EO-RS (Trazabilidad)
            missionRepository.save(SavingsMission.builder()
                    .title("Manifiesto MAN-RAEE-2026-001 (EcoRAEE Perú)")
                    .description("Lote de 15 servidores y 30 monitores entregados con Guía de Remisión GR-001-9482")
                    .targetAmount(new BigDecimal("500.00"))
                    .currentAmount(new BigDecimal("500.00"))
                    .deadline(LocalDate.now().plusDays(25))
                    .icon("local_shipping")
                    .isCompleted(true)
                    .user(valentino)
                    .build());

            missionRepository.save(SavingsMission.builder()
                    .title("Manifiesto MAN-RAEE-2026-002 (ReciclaTech S.A.)")
                    .description("Lote en acopio temporal pendiente de recojo y pesaje por EO-RS autorizada")
                    .targetAmount(new BigDecimal("300.00"))
                    .currentAmount(new BigDecimal("120.00"))
                    .deadline(LocalDate.now().plusDays(10))
                    .icon("pending_actions")
                    .isCompleted(false)
                    .user(valentino)
                    .build());
        }

        // 7. Usuario Invitado / Operador EO-RS (GUEST)
        if (!userRepository.existsByUsername("invitado")) {
            Set<Role> guestRoles = new HashSet<>();
            guestRoles.add(guestRole);

            User guest = User.builder()
                    .username("invitado")
                    .email("invitado@ecotechtrack.utp.pe")
                    .password(passwordEncoder.encode("Guest123!*"))
                    .fullName("Operador Técnico EO-RS Autorizada")
                    .points(0)
                    .streak(0)
                    .monthlyIncome(BigDecimal.ZERO)
                    .roles(guestRoles)
                    .build();

            userRepository.save(guest);
            logger.info("Usuario 'invitado' creado exitosamente (Clave: Guest123!*)");
        }

        // ===================================================================
        // 8. SEMILLERO ECOTECHTRACK (ALMACENES, EMPRESAS GESTORAS Y RESIDUOS)
        // ===================================================================
        if (almacenRepository.count() == 0) {
            Almacen almCentral = almacenRepository.save(Almacen.builder()
                    .codigo("ALM-TI-01")
                    .nombre("Almacén General de TI")
                    .sede("Sede Central - Sótano 1")
                    .direccion("Av. Arequipa 265, Lima")
                    .capacidadKg(5000.0)
                    .responsable("Rosa Salazar (Custodio TI)")
                    .build());

            Almacen almNorte = almacenRepository.save(Almacen.builder()
                    .codigo("ALM-TEMP-02")
                    .nombre("Depósito Temporal B")
                    .sede("Sede Norte - Módulo 3")
                    .direccion("Panamericana Norte Km 15")
                    .capacidadKg(2500.0)
                    .responsable("Carlos Ramos (Almacén)")
                    .build());

            EmpresaGestora eors1 = gestoraRepository.save(EmpresaGestora.builder()
                    .ruc("20556677881")
                    .razonSocial("EcoRAEE Perú S.A.C.")
                    .registroAutorizacion("REG-MINAM-EO-RS-004-2026")
                    .email("operaciones@ecoraee.pe")
                    .telefono("01-4458921")
                    .direccion("Av. Néstor Gambetta 1240, Callao")
                    .build());

            EmpresaGestora eors2 = gestoraRepository.save(EmpresaGestora.builder()
                    .ruc("20601234567")
                    .razonSocial("ReciclaTech Soluciones Ambientales S.A.")
                    .registroAutorizacion("REG-MINAM-EO-RS-012-2026")
                    .email("contacto@reciclatech.pe")
                    .telefono("01-7123456")
                    .direccion("Parque Industrial Huachipa Lote 14")
                    .build());

            // Residuos Electrónicos
            ResiduoElectronico r1 = residuoRepository.save(ResiduoElectronico.builder()
                    .codigoIdentificacion("RAEE-2026-001")
                    .descripcion("Servidor Torre HP ProLiant ML350 Gen9")
                    .categoria(CategoriaResiduo.EQUIPOS_INFORMATICA_Y_TELECOMUNICACIONES)
                    .marca("HP")
                    .modelo("ML350 Gen9")
                    .numeroSerie("SVR-HP-9981")
                    .pesoKg(32.4)
                    .estado(EstadoResiduo.DECLARADO_BAJA)
                    .observaciones("Baja técnica por obsolescencia. Placas con estaño/plomo.")
                    .almacen(almCentral)
                    .empresaGestora(eors1)
                    .build());

            ResiduoElectronico r2 = residuoRepository.save(ResiduoElectronico.builder()
                    .codigoIdentificacion("RAEE-2026-002")
                    .descripcion("Laptop Lenovo ThinkPad T480")
                    .categoria(CategoriaResiduo.EQUIPOS_INFORMATICA_Y_TELECOMUNICACIONES)
                    .marca("Lenovo")
                    .modelo("T480")
                    .numeroSerie("PF12A980")
                    .pesoKg(1.8)
                    .estado(EstadoResiduo.ALMACENADO_TEMPORAL)
                    .observaciones("Batería de litio/polímero intacta. Pantalla sin roturas.")
                    .almacen(almCentral)
                    .empresaGestora(eors1)
                    .build());

            ResiduoElectronico r3 = residuoRepository.save(ResiduoElectronico.builder()
                    .codigoIdentificacion("RAEE-2026-003")
                    .descripcion("Impresora Láser HP LaserJet Enterprise M605")
                    .categoria(CategoriaResiduo.EQUIPOS_INFORMATICA_Y_TELECOMUNICACIONES)
                    .marca("HP")
                    .modelo("M605")
                    .numeroSerie("PRN-HP-5542")
                    .pesoKg(21.3)
                    .estado(EstadoResiduo.SOLICITUD_RECOJO)
                    .observaciones("Asignada a EcoRAEE Perú para recojo y valorización.")
                    .almacen(almNorte)
                    .empresaGestora(eors1)
                    .build());

            // Trazabilidad de Auditoría (HistorialMovimiento)
            historialRepository.save(HistorialMovimiento.builder()
                    .estadoAnterior(null)
                    .estadoNuevo(EstadoResiduo.DECLARADO_BAJA)
                    .fechaMovimiento(LocalDateTime.now().minusDays(5))
                    .responsable("Jefe de TI (Admin Ambiental)")
                    .observaciones("Declaración formal de baja técnica por informe N° 04-TI")
                    .residuo(r1)
                    .build());

            historialRepository.save(HistorialMovimiento.builder()
                    .estadoAnterior(null)
                    .estadoNuevo(EstadoResiduo.DECLARADO_BAJA)
                    .fechaMovimiento(LocalDateTime.now().minusDays(3))
                    .responsable("Jefe de TI")
                    .observaciones("Baja de laptop por término de ciclo de vida contable")
                    .residuo(r2)
                    .build());

            historialRepository.save(HistorialMovimiento.builder()
                    .estadoAnterior(EstadoResiduo.DECLARADO_BAJA)
                    .estadoNuevo(EstadoResiduo.ALMACENADO_TEMPORAL)
                    .fechaMovimiento(LocalDateTime.now().minusDays(2))
                    .responsable("Rosa Salazar (Almacén)")
                    .observaciones("Ingreso al Almacén General de TI y pesaje oficial (1.8 kg)")
                    .residuo(r2)
                    .build());

            logger.info(">>> Semillas de EcoTechTrack (Almacenes, EO-RS, Residuos e Historiales) cargadas exitosamente.");
        }

        logger.info(">>> Datos semilla cargados con éxito.");
    }
}
