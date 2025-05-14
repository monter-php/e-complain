ALTER TABLE complains ADD COLUMN product_id VARCHAR(255) NOT NULL DEFAULT 'UNKNOWN';

-- Update existing rows to have a default productId if necessary, or handle as per application logic
-- For example, if you want to allow NULL initially and then populate, the above would be different.
-- If productId should always have been there and existing data needs a placeholder:
-- UPDATE complains SET product_id = 'UNKNOWN' WHERE product_id IS NULL;
-- However, the NOT NULL DEFAULT 'UNKNOWN' handles new rows and rows existing before this script if they were to be created by an older app version without this field.
-- For a live system, you might need a more sophisticated data migration strategy for existing rows.

-- If you decide productId can be nullable, then:
-- ALTER TABLE complains ADD COLUMN product_id VARCHAR(255);
