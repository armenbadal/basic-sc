
' Greatest common divisor
SUB Gcd(n, m)
  WHILE n <> m
    IF n > m THEN
      n = n - m
    ELSE
      m = m - n
    END IF
  END WHILE
  Gcd = n
END SUB

SUB Main()
  PRINT Gcd(23, 12)
  PRINT Gcd(15, 45)
  PRINT Gcd(3, 21)
END SUB

