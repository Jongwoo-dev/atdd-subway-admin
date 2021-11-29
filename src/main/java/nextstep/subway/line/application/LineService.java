package nextstep.subway.line.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nextstep.subway.exception.SubwayError;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(() -> new SubwayException(
            SubwayError.NOT_FOUND_DATA));
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(() -> new SubwayException(
            SubwayError.NOT_FOUND_DATA));
        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<LineResponse> findLineById(long id) {
        Optional<Line> line = lineRepository.findById(id);
        return line.map(LineResponse::of);
    }

    public void updateLine(Line newLine) {
        Line line = lineRepository.findById(newLine.getId()).orElseThrow(() -> new SubwayException(
            SubwayError.NOT_FOUND_DATA));
        line.update(newLine);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
