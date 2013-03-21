PACKAGE rteasy_functions IS
  FUNCTION bool_signed_lt (a, b : std_logic_vector; sign_index : natural)
    RETURN boolean;
  FUNCTION signed_lt (a, b : std_logic_vector; sign_index : natural)
    RETURN std_logic_vector;
  FUNCTION signed_le (a, b : std_logic_vector; sign_index : natural)
    RETURN std_logic_vector;
  FUNCTION signed_gt (a, b : std_logic_vector; sign_index : natural)
    RETURN std_logic_vector;
  FUNCTION signed_ge (a, b : std_logic_vector; sign_index : natural)
    RETURN std_logic_vector;
  FUNCTION signed_eq (a, b : std_logic_vector) RETURN std_logic_vector;
  FUNCTION signed_ne (a, b : std_logic_vector) RETURN std_logic_vector;
END rteasy_functions;

PACKAGE BODY rteasy_functions IS

  -- signed relative comparison functions
  FUNCTION bool_signed_lt (a, b : std_logic_vector; sign_index : natural)
    RETURN boolean IS
  BEGIN
    IF a(sign_index) = b(sign_index) THEN
      RETURN a < b; 
    ELSE
      RETURN a(sign_index) = '1';
    END IF;
  END bool_signed_lt;

  FUNCTION signed_lt (a, b : std_logic_vector; sign_index : natural)
    RETURN std_logic_vector IS
  BEGIN
    IF bool_signed_lt(a,b,sign_index) THEN RETURN "1";
    ELSE RETURN "0";
    END IF;
  END signed_lt;

  FUNCTION signed_le (a, b : std_logic_vector; sign_index : natural)
    RETURN std_logic_vector IS
  BEGIN
    IF (a = b) OR bool_signed_lt(a,b,sign_index) THEN RETURN "1";
    ELSE RETURN "0";
    END IF;
  END signed_le;

  FUNCTION signed_gt (a, b : std_logic_vector; sign_index : natural)
    RETURN std_logic_vector IS
  BEGIN
    IF (a = b) OR bool_signed_lt(a,b,sign_index) THEN RETURN "0";
    ELSE RETURN "1";
    END IF;
  END signed_gt;

  FUNCTION signed_ge (a, b : std_logic_vector; sign_index : natural)
    RETURN std_logic_vector IS
  BEGIN
    IF bool_signed_lt(a,b,sign_index) THEN RETURN "0";
    ELSE RETURN "1";
    END IF;
  END signed_ge;

  FUNCTION signed_eq (a, b : std_logic_vector) RETURN std_logic_vector IS
  BEGIN
    IF a = b THEN RETURN "1";
    ELSE RETURN "0";
    END IF;
  END signed_eq;

  FUNCTION signed_ne (a, b : std_logic_vector) RETURN std_logic_vector IS
  BEGIN
    IF a = b THEN RETURN "0";
    ELSE RETURN "1";
    END IF;
  END signed_ne;

END rteasy_functions;