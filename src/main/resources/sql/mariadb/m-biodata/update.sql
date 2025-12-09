update m_biodata 
set id=#{id},fullname=#{fullname},mobile_phone=#{mobilePhone},image=#{image},image_path=#{imagePath},modified_by=#{modifiedBy},modified_on=#{modifiedOn},deleted_by=#{deletedBy},deleted_on=#{deletedOn},is_delete=#{isDelete} 
where id=#{id}