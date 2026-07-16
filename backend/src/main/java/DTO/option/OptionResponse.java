package DTO.option;

/**
 * Response DTO representing a single key-value attribute (option) of an advertisement.
 *
 * <p>Returned as part of advertisement detail responses to describe structured
 * metadata such as "RAM = 8 GB" or "Color = Midnight Black".</p>
 *
 * @param id     the surrogate identifier of the option record
 * @param option the attribute key name (e.g., "RAM", "Color")
 * @param value  the attribute value (e.g., "8 GB", "Midnight Black")
 */
public record OptionResponse(

        /** Surrogate database identifier for this option. */
        Long id,

        /** The name of the attribute key. */
        String option,

        /** The value assigned to the attribute key. */
        String value

) {}
