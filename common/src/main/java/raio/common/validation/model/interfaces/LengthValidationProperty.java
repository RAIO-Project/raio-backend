package raio.common.validation.model.interfaces;

public interface LengthValidationProperty extends BaseValidationProperty {
    Integer minLength();
    Integer maxLength();
}
