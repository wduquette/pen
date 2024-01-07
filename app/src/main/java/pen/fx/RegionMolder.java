package pen.fx;

import javafx.scene.layout.Region;

@SuppressWarnings("unused")
public record RegionMolder(Region object)
    implements RegionMolderBase<Region, RegionMolder>
{
    // See RegionMolderBase for setters
}
