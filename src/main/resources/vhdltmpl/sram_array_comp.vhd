COMPONENT sram_array
  GENERIC(addr_width, data_width : positive);
  PORT(
    CS, WE     : IN  std_logic;
    SELECT_ALL : IN  std_logic;
    ADDR       : IN  std_logic_vector(addr_width-1 DOWNTO 0);
    DATA_IN    : IN  std_logic_vector(data_width-1 DOWNTO 0);
    DATA_OUT   : OUT std_logic_vector(data_width-1 DOWNTO 0)
  );
END COMPONENT;

FOR ALL : sram_array USE ENTITY WORK.sram_array(primitive);
