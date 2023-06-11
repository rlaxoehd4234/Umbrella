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

    @GetMapping(value = "/workspace/{workspace_id}")
    public ResponseEntity<?> findById(@PathVariable("workspace_id") Long id){
        return ResponseEntity.ok().body(workSpaceService.findById(id));
    }

    @PutMapping(value = "/workspace/{workspace_id}")
    public ResponseEntity<?> update(@PathVariable Long workspace_id, @RequestBody WorkspaceUpdateRequestDto requestDto){
        return ResponseEntity.ok().body(workSpaceService.update(workspace_id,requestDto));
    }

//    @DeleteMapping(value = "/workspace/{workspace_id}")
//    public ResponseEntity<?> delete(@PathVariable Long workspace_id){
//        return ResponseEntity.ok().body(workSpaceService.delete(workspace_id));
//    }

    @GetMapping(value = "/workspace")
    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok().body(workSpaceService.findAllList());
    }
}
