package shared.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddLineCmd(
        @NotNull Long coffeeId,
        int qty          // no @Min
) { }
