$ErrorActionPreference = "Stop"

$Jar = "Final-Proj\Final-Proj\vmsimulation.jar"
$Samples = "Final-Proj\Final-Proj"
$OutDir = "test-output"

if (-not (Test-Path $Jar)) {
    throw "No se encontro $Jar"
}

New-Item -ItemType Directory -Path $OutDir -Force | Out-Null
Remove-Item -Path "vmmanager\*.class" -Force -ErrorAction SilentlyContinue

Write-Host "Compilando vmmanager..."
javac -cp $Jar vmmanager\*.java
if ($LASTEXITCODE -ne 0) {
    throw "La compilacion fallo"
}

$OfficialTests = @(
    @("V0","16","20","0","q0_outputsample_1.txt"),
    @("V0","16","20","42","q0_outputsample_2.txt"),
    @("V0","1024","70","10","q0_outputsample_3.txt"),
    @("V0","2048","800","20","q0_outputsample_4.txt"),
    @("V0","4096","400","77","q0_outputsample_5.txt"),
    @("V0","32","2000","167","q0_outputsample_6.txt"),
    @("V1","16","16","4","20","0","q1_outputsample_1.txt"),
    @("V1","16","16","4","20","1","q1_outputsample_2.txt"),
    @("V1","1024","1024","16","20","2","q1_outputsample_3.txt"),
    @("V1","2048","2048","32","100","20","q1_outputsample_4.txt"),
    @("V1","4096","4096","256","200","21","q1_outputsample_5.txt"),
    @("V1","32","32","8","2000","22","q1_outputsample_6.txt"),
    @("V2","16","32","4","10","0","0","q2_outputsample_1.txt"),
    @("V2","16","64","4","20","1","10","q2_outputsample_2.txt"),
    @("V2","1024","4096","16","120","2","0","q2_outputsample_3.txt"),
    @("V2","2048","4096","8","100","20","0","q2_outputsample_4.txt"),
    @("V2","2048","4096","256","200","30","0","q2_outputsample_5.txt"),
    @("V2","32","64","8","2000","33","1","q2_outputsample_6.txt"),
    @("V3","16","32","4","10","0","0","q3_outputsample_1.txt"),
    @("V3","16","64","4","20","1","10","q3_outputsample_2.txt"),
    @("V3","1024","4096","16","120","2","0","q3_outputsample_3.txt"),
    @("V3","2048","4096","8","100","20","0","q3_outputsample_4.txt"),
    @("V3","2048","4096","256","200","30","0","q3_outputsample_5.txt"),
    @("V3","32","64","8","2000","33","1","q3_outputsample_6.txt"),
    @("V4","16","32","4","10","0","0","q4_outputsample_1.txt"),
    @("V4","16","64","4","20","1","10","q4_outputsample_2.txt"),
    @("V4","1024","4096","16","120","2","0","q4_outputsample_3.txt"),
    @("V4","2048","4096","8","100","20","0","q4_outputsample_4.txt"),
    @("V4","2048","4096","256","200","30","0","q4_outputsample_5.txt"),
    @("V4","32","64","8","2000","33","1","q4_outputsample_6.txt")
)

Write-Host "Ejecutando pruebas oficiales con diff..."
$Failures = @()
foreach ($Test in $OfficialTests) {
    $Sample = $Test[-1]
    $ArgsList = $Test[0..($Test.Length - 2)]
    $Actual = Join-Path $OutDir $Sample
    $Expected = Join-Path $Samples $Sample

    java -cp ".;$Jar" vmsimulation.Simulator @ArgsList > $Actual
    if ($LASTEXITCODE -ne 0) {
        $Failures += "$Sample fallo al ejecutar java"
        continue
    }

    $Diff = Compare-Object -ReferenceObject (Get-Content $Expected) -DifferenceObject (Get-Content $Actual) -SyncWindow 0
    if ($Diff) {
        $Failures += "$Sample tiene diferencias"
    }
}

if ($Failures.Count -gt 0) {
    $Failures | ForEach-Object { Write-Host $_ }
    throw "Fallaron pruebas oficiales"
}
Write-Host "Pruebas oficiales: PASS"

$StressTests = @(
    @("stress_v2.txt", "V2", "16", "4096", "4", "20000", "123", "0"),
    @("stress_v3.txt", "V3", "16", "4096", "4", "20000", "123", "0"),
    @("stress_v4.txt", "V4", "16", "4096", "4", "20000", "123", "0"),
    @("locality_v3.txt", "V3", "64", "4096", "8", "50000", "777", "80"),
    @("locality_v4.txt", "V4", "64", "4096", "8", "50000", "777", "80"),
    @("big_v0.txt", "V0", "4096", "100000", "999"),
    @("big_v1.txt", "V1", "8192", "8192", "16", "50000", "999"),
    @("brutal_v4.txt", "V4", "32", "8192", "4", "100000", "42", "0")
)

Write-Host "Ejecutando pruebas intensivas..."
foreach ($Test in $StressTests) {
    $FileName = $Test[0]
    $ArgsList = $Test[1..($Test.Length - 1)]
    $Actual = Join-Path $OutDir $FileName

    Write-Host "  java vmsimulation.Simulator $($ArgsList -join ' ')"
    java -cp ".;$Jar" vmsimulation.Simulator @ArgsList > $Actual
    if ($LASTEXITCODE -ne 0) {
        throw "$FileName fallo al ejecutar java"
    }
}

Write-Host "Resumen de contadores:"
Get-ChildItem $OutDir -Filter "*.txt" |
    Where-Object { $_.Name -like "stress_*" -or $_.Name -like "locality_*" -or $_.Name -like "brutal_*" -or $_.Name -like "big_v1.txt" } |
    ForEach-Object {
        $Counters = Select-String -Path $_.FullName -Pattern "NUM PAGE FAULTS|NUM BYTES TRANSFERRED"
        Write-Host $_.Name
        $Counters | ForEach-Object { Write-Host "  $($_.Line)" }
    }

Write-Host "Pruebas intensivas: PASS"
Write-Host "Salidas generadas en: $OutDir"
