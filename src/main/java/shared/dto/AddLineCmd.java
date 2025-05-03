package shared.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddLineCmd(
        @NotNull Long coffeeId,   // or UUID if you moved to UUID IDs
        @Min(1) int qty
) {}
