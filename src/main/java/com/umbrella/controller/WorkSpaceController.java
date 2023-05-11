package com.umbrella.controller;

import com.umbrella.dto.workspace.WorkspaceRequestDto;
import com.umbrella.dto.workspace.WorkspaceUpdateRequestDto;
import com.umbrella.service.Impl.WorkSpaceServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Getter
@RequiredArgsConstructor
public class WorkSpaceController {

    private final WorkSpaceServiceImpl workSpaceService;

    @PostMapping(value = "/workspace")
    public ResponseEntity<?> save(@Valid @RequestBody WorkspaceRequestDto requestDto){
        return ResponseEntity.ok().body(workSpaceService.save(requestDto));
    }

    @GetMapping(value = "/workspace/{workspace_id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(workSpaceService.findById(id));
    }

    @PutMapping(value = "/workspace/{workspace_id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody WorkspaceUpdateRequestDto requestDto){
        return ResponseEntity.ok().body(workSpaceService.update(id,requestDto));
    }

    @DeleteMapping(value = "/workspace/{workspace_id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return ResponseEntity.ok().body(workSpaceService.delete(id));
    }

    @GetMapping(value = "/workspace")
    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok().body(workSpaceService.findAllList());
    }
}
