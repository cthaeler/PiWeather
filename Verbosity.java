
/**
 * Enumeration class Verbosity - write a description of the enum class here
 *
 * @author Charles Thaeler
 * @version 0.2
 */

public enum Verbosity {
    ShowSilent,
    ShowExceptions,
    ShowErrors,
    ShowInformation,
    ShowEverything;
    
    boolean ShowSilent() {
        return this.ordinal() == Verbosity.ShowSilent.ordinal();
    }
    boolean ShowExceptions() {
        return this.ordinal() > Verbosity.ShowSilent.ordinal();
    }
    boolean ShowErrors() {
        return this.ordinal() > Verbosity.ShowExceptions.ordinal();
    }
    boolean ShowInformation() {
        return this.ordinal() > Verbosity.ShowErrors.ordinal();
    }
    boolean ShowEverything() {
        return this.ordinal() > Verbosity.ShowInformation.ordinal();
    }
}
