// package ar.utn.ccaffa.services;

// import com.mongodb.client.gridfs.model.GridFSFile;
// import org.bson.types.ObjectId;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.data.mongodb.gridfs.GridFsOperations;
// import org.springframework.data.mongodb.gridfs.GridFsTemplate;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;

// @Service
// public class ImagenService {
    
//     @Autowired
//     private GridFsTemplate gridFsTemplate;
    
//     @Autowired
//     private GridFsOperations operations;

//     public String guardarImagen(MultipartFile archivo) throws IOException {
//         ObjectId id = gridFsTemplate.store(
//             archivo.getInputStream(),
//             archivo.getOriginalFilename(),
//             archivo.getContentType()
//         );
//         return id.toString();
//     }

//     public byte[] obtenerImagen(String id) throws IOException {
//         GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
//         if (file == null) {
//             throw new IOException("Imagen no encontrada");
//         }
//         return operations.getResource(file).getContent().readAllBytes();
//     }

//     public void eliminarImagen(String id) {
//         gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
//     }
// }