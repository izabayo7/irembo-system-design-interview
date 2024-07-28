package rw.companyz.useraccountms.clients;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import rw.companyz.useraccountms.exceptions.ResourceNotFoundException;
import rw.companyz.useraccountms.config.FeignConfig;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "citizenClient", configuration = FeignConfig.class)
public interface CitizensFeignClient {
//    @GetMapping(value = "/api/v1/locationAddresses/locationType/{locationType}/as-list", produces = MediaType.APPLICATION_JSON_VALUE)
//    ApiResponse<List<CitizenLocationAddress>> getLocationsByLocationType(@PathVariable @Valid @NotNull ELocationType locationType);
}
