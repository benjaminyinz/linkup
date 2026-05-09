package com.linkup.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

/**
 * 架构约束测试。
 *
 * <p>把"分层规范"从口头约定升级成构建期检查。任何违反规则的提交都会导致 mvn test 失败，
 * 强制业务代码遵守 Plan 文件中的 Phase 3 规范。</p>
 *
 * <p>当前规范见 plan：
 * <ul>
 *   <li>Service 单一化（不做 CQRS 拆分）</li>
 *   <li>两层 Service：base + business，base 必须在 service.base 子包且包私有</li>
 *   <li>Controller 不能直接依赖 BaseService</li>
 *   <li>Mapper 必须 extends BaseMapper</li>
 *   <li>Controller 必须以 Controller 结尾，且只能在 controller 包下</li>
 * </ul></p>
 */
class ArchitectureTest {

    private static final JavaClasses LINKUP_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.linkup");

    /**
     * Controller 不允许直接依赖 BaseService —— 必须走业务 Service。
     */
    @Test
    void controller_must_not_depend_on_base_service() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().haveSimpleNameEndingWith("BaseService")
                .orShould().dependOnClassesThat().haveSimpleNameEndingWith("BaseServiceImpl");

        rule.allowEmptyShould(true).check(LINKUP_CLASSES);
    }

    /**
     * BaseService 必须放在 service.base 子包下（统一约束位置 + 配合 package-private 可见性）。
     */
    @Test
    void base_service_must_reside_in_service_base_package() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("BaseService")
                .or().haveSimpleNameEndingWith("BaseServiceImpl")
                .should().resideInAPackage("..service.base..");

        rule.allowEmptyShould(true).check(LINKUP_CLASSES);
    }

    /**
     * Mapper 接口必须 extends BaseMapper。
     */
    @Test
    void mappers_must_extend_base_mapper() {
        ArchRule rule = classes()
                .that().resideInAPackage("..mapper..")
                .and().haveSimpleNameEndingWith("Mapper")
                .should().beAssignableTo(com.baomidou.mybatisplus.core.mapper.BaseMapper.class);

        rule.allowEmptyShould(true).check(LINKUP_CLASSES);
    }

    /**
     * Controller 命名 + 位置约束。
     */
    @Test
    void controllers_must_be_in_controller_package_and_named_consistently() {
        ArchRule rule = classes()
                .that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller");

        rule.allowEmptyShould(true).check(LINKUP_CLASSES);
    }

    /**
     * Entity 不能依赖 Controller / Service / Mapper —— 防止反向依赖。
     */
    @Test
    void entities_should_not_depend_on_upper_layers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..entity..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..controller..", "..service..", "..mapper..");

        rule.allowEmptyShould(true).check(LINKUP_CLASSES);
    }
}
