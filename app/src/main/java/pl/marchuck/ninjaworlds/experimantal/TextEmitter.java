package pl.marchuck.ninjaworlds.experimantal;

/**
 * @author Lukasz Marczak
 * @since 27.08.16.
 */
public interface TextEmitter {
    rx.Observable<CharSequence> emit();
}
