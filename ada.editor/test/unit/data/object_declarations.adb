--
-- 3.3.1 Object Declarations
--
-- object_declaration ::=
--   defining_identifier_list : [aliased] [constant] subtype_indication [:= expression];
--  | defining_identifier_list : [aliased] [constant] array_type_definition [:= expression];
--  | single_task_declaration
--  | single_protected_declaration
--
-- defining_identifier_list ::=
-- defining_identifier {, defining_identifier}
--
-- NOTE: This module is not compilation is used only for testing purposes
--

procedure Object_Declaratios is

    -- Example of a multiple object declaration:

    -- the multiple object declaration
    John, Paul : Person_Name := new Person(Sex => M); -- see 3.10.1

    -- is equivalent to the two single object declarations in the order given
    John2 : Person_Name := new Person(Sex => M);
    Paul2 : Person_Name := new Person(Sex => M);

    -- Examples of variable declarations:
    Count, Sum : Integer;
    Size : Integer range 0 .. 10_000 := 0;
    Sorted : Boolean := False;
    Color_Table : array(1 .. Max) of Color;
    Option : Bit_Vector(1 .. 10) := (others => True);
    Hello : constant String := "Hi, world.";

    -- Examples of constant declarations:
    Limit : constant Integer := 10_000;
    Low_Limit : constant Integer := Limit/10;
    Tolerance : constant Real := Dispersion(1.15);

begin

    null;

end Object_Declaration;