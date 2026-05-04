package id.primaraya.qcontrol.data.remote.pemetaan

import id.primaraya.qcontrol.data.remote.dto.*
import id.primaraya.qcontrol.ranah.model.*

fun LineProduksiDto.keDomain(): LineProduksi = LineProduksi(
    id = id,
    kodeLine = kodeLine,
    namaLine = namaLine,
    aktif = aktif,
    urutanTampil = urutanTampil
)

fun SlotWaktuDto.keDomain(): SlotWaktu = SlotWaktu(
    id = id,
    kodeSlot = kodeSlot,
    labelSlot = labelSlot,
    jamMulai = jamMulai,
    jamSelesai = jamSelesai,
    aktif = aktif,
    urutanTampil = urutanTampil
)

fun MaterialDto.keDomain(): Material = Material(
    id = id,
    kodeMaterial = kodeMaterial,
    namaMaterial = namaMaterial,
    aktif = aktif
)

fun PartDto.keDomain(): Part = Part(
    id = id,
    kodeUnikPart = kodeUnikPart,
    namaPart = namaPart,
    nomorPart = nomorPart,
    materialId = materialId,
    kodeMaterial = kodeMaterial,
    namaMaterial = namaMaterial,
    kodeProyek = kodeProyek,
    jumlahItemPerKanban = jumlahItemPerKanban,
    lineDefaultId = lineDefaultId,
    kodeLineDefault = kodeLineDefault,
    namaLineDefault = namaLineDefault,
    aktif = aktif,
    sumberData = sumberData
)

fun KategoriDefectDto.keDomain(): KategoriDefect = KategoriDefect(
    id = id,
    kodeKategori = kodeKategori,
    namaKategori = namaKategori,
    aktif = aktif,
    urutanTampil = urutanTampil
)

fun JenisDefectDto.keDomain(): JenisDefect = JenisDefect(
    id = id,
    kodeDefect = kodeDefect,
    namaDefect = namaDefect,
    kategoriDefectId = kategoriDefectId,
    kodeKategori = kodeKategori,
    namaKategori = namaKategori,
    aktif = aktif
)

fun RelasiPartDefectDto.keDomain(): RelasiPartDefect = RelasiPartDefect(
    id = id,
    partId = partId,
    kodeUnikPart = kodeUnikPart,
    jenisDefectId = jenisDefectId,
    kodeDefect = kodeDefect,
    urutanTampil = urutanTampil,
    aktif = aktif
)

fun MasterDataQControlDto.keDomain(jumlahShiftOperasional: Int): MasterDataQControl = MasterDataQControl(
    versiMasterData = versiMasterData,
    lineProduksi = lineProduksi.map { it.keDomain() },
    slotWaktu = slotWaktu.map { it.keDomain() },
    material = material.map { it.keDomain() },
    part = part.map { it.keDomain() },
    kategoriDefect = kategoriDefect.map { it.keDomain() },
    jenisDefect = jenisDefect.map { it.keDomain() },
    relasiPartDefect = relasiPartDefect.map { it.keDomain() },
    jumlahShiftOperasional = jumlahShiftOperasional
)
