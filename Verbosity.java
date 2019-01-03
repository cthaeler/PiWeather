
/**
 * Enumeration class Verbosity - write a description of the enum class here
 *
 * @author Charles Thaeler
 * @version 0.2
 */

public enum Verbosity {
    ShowSilent,
    ShowErrors,
    ShowStackTrace,
    ShowInformation,
    ShowEverything,
    ShowDebugging;
    
    boolean ShowSilent() {
        return this.ordinal() == Verbosity.ShowSilent.ordinal();
    }

    boolean ShowErrors() {
        return this.ordinal() > Verbosity.ShowSilent.ordinal();
    }
    
    boolean ShowStackTrace() {
        return this.ordinal() > Verbosity.ShowErrors.ordinal();
    }
    
    boolean ShowInformation() {
        return this.ordinal() > Verbosity.ShowStackTrace.ordinal();
    }
    
    boolean ShowEverything() {
        return this.ordinal() > Verbosity.ShowInformation.ordinal();
    }
     
    boolean ShowDebugging() {
        return this.ordinal() > Verbosity.ShowEverything.ordinal();
    }
}
