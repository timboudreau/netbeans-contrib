--
-- Comment for <code>Main</code> procedure.
-- @author Andrea Lucarelli
--

with Ada.Text_Io;                       -- With package Text_Io
use Ada.Text_Io;                        -- Use components

procedure Main is
    Count : Integer;                    -- Declaration of count
begin
    Count := 10;                        -- Set to 10
    while Count > 0 loop                -- loop while greater than 0
        if Count = 3 then               -- If 3 print Ignition
            Put("Ignition"); New_Line;
        end if;
        Put( Integer'Image( Count ) );  -- Print current count
        New_Line;
        Count := Count - 1;             -- Decrement by 1 count
        delay 1.0;                      -- Wait 1 second
    end loop;
    Put("Blast off"); New_Line;         -- Print Blast off
end Main;