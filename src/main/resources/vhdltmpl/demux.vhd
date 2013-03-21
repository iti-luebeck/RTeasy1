LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY demux IS
  GENERIC(
    select_width, line_width : positive;
    default_out : std_logic
  );
  PORT(
    INPUT  : IN  std_logic_vector(line_width-1 DOWNTO 0);
    SEL    : IN  std_logic_vector(select_width-1 DOWNTO 0);
    FLOOD  : IN  std_logic; -- FLOOD=1 causes all bits of OUTPUT to be set to 1
    OUTPUT : OUT std_logic_vector(2**select_width*line_width-1 DOWNTO 0)
  );
END demux;

ARCHITECTURE recursive OF demux IS
  SIGNAL subdemux_0_IN, subdemux_1_IN : std_logic_vector(line_width-1 DOWNTO 0);

  COMPONENT demux
    GENERIC(
      select_width, line_width : positive;
      default_out : std_logic
    );
    PORT(
      INPUT  : IN  std_logic_vector(line_width-1 DOWNTO 0);
      SEL    : IN  std_logic_vector(select_width-1 DOWNTO 0);
      FLOOD  : IN  std_logic; -- FLOOD=1 causes all bits of OUTPUT to be set to 1
      OUTPUT : OUT std_logic_vector(2**select_width*line_width-1 DOWNTO 0)
    );
  END COMPONENT;

  FOR ALL : demux USE ENTITY WORK.demux(recursive);

BEGIN
  demux1to2: IF select_width=1 GENERATE
    OUTPUT(line_width-1 DOWNTO 0) <= (OTHERS => '1') WHEN FLOOD='1'
      ELSE INPUT WHEN SEL="0" ELSE (OTHERS => default_out);
    OUTPUT(2*line_width-1 DOWNTO line_width) <= (OTHERS => '1') WHEN FLOOD='1'
      ELSE INPUT WHEN SEL="1" ELSE (OTHERS => default_out);
  END GENERATE;

  demux1toN: IF select_width>1 GENERATE
    subdemux_0: demux
    GENERIC MAP(select_width => select_width-1, line_width => line_width,
                default_out => default_out)
    PORT MAP(INPUT  => subdemux_0_IN,
             SEL    => SEL(select_width-2 DOWNTO 0),
             FLOOD  => FLOOD,
             OUTPUT => OUTPUT(2**(select_width-1)*line_width-1 DOWNTO 0));

    subdemux_1: demux
    GENERIC MAP(select_width => select_width-1, line_width => line_width,
                default_out => default_out)
    PORT MAP(INPUT  => subdemux_1_IN,
             SEL    => SEL(select_width-2 DOWNTO 0),
             FLOOD  => FLOOD,
             OUTPUT => OUTPUT(2**select_width*line_width-1
                              DOWNTO 2**(select_width-1)*line_width));

    subdemux_0_IN <= INPUT WHEN SEL(select_width-1)='0'
                     ELSE (OTHERS => default_out);
    subdemux_1_IN <= INPUT WHEN SEL(select_width-1)='1'
                     ELSE (OTHERS => default_out);
  END GENERATE;
END recursive;
