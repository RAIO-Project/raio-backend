package raio.video.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import raio.video.application.usecase.UploadVideoUseCase;
import raio.video.web.request.VideoUploadRequest;
import raio.video.web.response.VideoUploadResponse;

import java.io.IOException;

@Tag(name = "Video", description = "동영상 관련 API")
@RestController
@RequestMapping("/videos")
public class VideoApi {

    private final UploadVideoUseCase uploadVideoUseCase;

    public VideoApi(UploadVideoUseCase uploadVideoUseCase) {
        this.uploadVideoUseCase = uploadVideoUseCase;
    }

    @Operation(summary = "동영상 업로드", description = "JWT 인증 후 동영상 파일을 서버에 업로드. 저장된 videoId 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 파일 형식"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
            @ApiResponse(responseCode = "500", description = "파일 저장 실패")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public VideoUploadResponse upload(
            @RequestPart MultipartFile file,
            @RequestParam(required = false) String title,
            Authentication authentication
    ) throws IOException {
        Long uploaderId = Long.parseLong(authentication.getName());
        VideoUploadRequest request = new VideoUploadRequest(file, title);
        Long videoId = uploadVideoUseCase.upload(request.toCommand(uploaderId));
        return new VideoUploadResponse(videoId);
    }
}
