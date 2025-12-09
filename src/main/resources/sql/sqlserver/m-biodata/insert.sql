insert into 
m_biodata (id,fullname,mobile_phone,image,image_path,created_by,created_on,modified_by,modified_on,deleted_by,deleted_on,is_delete) 
values (#{id},#{fullname},#{mobilePhone},CONVERT(VARBINARY(MAX), #{image}),#{imagePath},#{createdBy},#{createdOn},#{modifiedBy},#{modifiedOn},#{deletedBy},#{deletedOn},#{isDelete})